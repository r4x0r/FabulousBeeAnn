import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_View_Feedbacks extends HttpServlet {  // JDK 6 and above only
	int breakline = 40;
	int offset = 25;

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement queryfeedbacks = null;
		String queryStr = null;
		String dataString = "";
		String error = "";
		boolean exists = false;

		//for file IO
		BufferedReader reader;
		PrintWriter writer;

		try {
			// Step 1: Allocate a database Connection object
			conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!
			// database-URL(hostname, port, default database), username, password

			// Step 2: Allocate a Statement object within the Connection
			queryfeedbacks = conn.createStatement();
			
			// check view by top feedbacks or view all feedbacks
			String view_by = request.getParameter("view");
			if (view_by.equals("View")) {
				
				// check if number is valid
				int top_num = Integer.parseInt(request.getParameter("top_num"));
				if (top_num > 0) {
					
					// view by top feedbacks
					queryStr = "SELECT user_id, score, comment, date, " +
							   "(useful + (very_useful*2.0)) / (useless + useful + very_useful) AS avg_rate, " + 
							   "useless + useful + very_useful AS num_voters " +
							   "FROM (SELECT * FROM Feedbacks " + 
							   "WHERE book_id = '" + request.getParameter("ISBN") + "' " + 
							   "AND (useless + useful + very_useful) > 0) book_comments " + 
							   "ORDER BY avg_rate DESC;";
					
					System.out.println(queryStr);
					
					ResultSet searchResult = queryfeedbacks.executeQuery(queryStr);
					
					while (searchResult.next() & (top_num > 0)) {
						dataString = dataString + "              <tr>";
						dataString = dataString + "\n                <td>" + searchResult.getString("user_id");
						dataString = dataString + "\n                <td>" + searchResult.getInt("score");
						dataString = dataString + "\n                <td>" + searchResult.getString("comment");
						dataString = dataString + "\n                <td>" + searchResult.getDate("date");
						dataString = dataString + "\n                <td>" + searchResult.getFloat("avg_rate");
						dataString = dataString + "\n                <td>" + searchResult.getInt("num_voters");
						dataString = dataString + "\n              <tr>\n";
						
						top_num --;
					}
					
					// if the feedbacks to be displayed is still less than the desired number
					if (top_num > 0) {
						queryStr = "SELECT user_id, score, comment, date, 0 AS avg_rate, 0 AS num_voters " +
								   "FROM (SELECT * FROM Feedbacks " + 
								   "WHERE book_id = '" + request.getParameter("ISBN") + "' " + 
								   "AND (useless + useful + very_useful) = 0) book_comments;";
						
						System.out.println(queryStr);
						
						searchResult = queryfeedbacks.executeQuery(queryStr);
						
						while (searchResult.next() & (top_num > 0)) {
							dataString = dataString + "              <tr>";
							dataString = dataString + "\n                <td>" + searchResult.getString("user_id");
							dataString = dataString + "\n                <td>" + searchResult.getInt("score");
							dataString = dataString + "\n                <td>" + searchResult.getString("comment");
							dataString = dataString + "\n                <td>" + searchResult.getDate("date");
							dataString = dataString + "\n                <td>" + searchResult.getInt("avg_rate");
							dataString = dataString + "\n                <td>" + searchResult.getInt("num_voters");
							dataString = dataString + "\n              <tr>\n";
							
							top_num --;
						}
					}
					
					exists = true;

				} else {
					error = "Invalid number input. Please refine your input.";
					
					out.println("<html><body><script type=\"text/javascript\">");  
					out.println("alert('" + error + "');"); 
					out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_feedback.html\";");
					out.println("</script></body></html>");
				}
				
			} else if (view_by.equals("View All Feedbacks")) {
				queryStr = "SELECT user_id, score, comment, date, " +
						   "(useful + (very_useful*2.0)) AS total_rate, " + 
						   "useless + useful + very_useful AS num_voters " +
						   "FROM Feedbacks " +
						   "WHERE book_id = '" + request.getParameter("ISBN") + "';";
				
				System.out.println(queryStr);
				
				ResultSet searchResult = queryfeedbacks.executeQuery(queryStr);
				
				while (searchResult.next()) {
					
					dataString = dataString + "              <tr>";
					dataString = dataString + "\n                <td>" + searchResult.getString("user_id");
					dataString = dataString + "\n                <td>" + searchResult.getInt("score");
					dataString = dataString + "\n                <td>" + searchResult.getString("comment");
					dataString = dataString + "\n                <td>" + searchResult.getDate("date");

					//averaging of the rating
					int num_of_raters = searchResult.getInt("num_voters");
					if (num_of_raters == 0) {
						dataString = dataString + "\n                <td>" + searchResult.getFloat("total_rate");
					} else {
						dataString = dataString + "\n                <td>" + (searchResult.getFloat("total_rate")/num_of_raters);
					}

					dataString = dataString + "\n                <td>" + num_of_raters;
					dataString = dataString + "\n              <tr>\n";
				}
				
				exists = true;
			}
			
			if (exists) {

				String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
				reader = new BufferedReader(new FileReader(filepathString + "user_view_feedbacks_template.html"));
				writer = new PrintWriter(filepathString + "user_view_feedbacks.html");

				int i;
				String outputString = "";
				for (i = 0; i < breakline; i ++) {
					outputString = outputString + reader.readLine() + "\n";
				}

				outputString = outputString + "                <input type=\"hidden\" name=\"ISBN\" value=\"" + request.getParameter("ISBN") + "\">\n";

				for (i = 0; i < offset; i ++) {
					outputString = outputString + reader.readLine() + "\n";
				}
				
				outputString = outputString + dataString;
				
				String endingString;
				while ((endingString = reader.readLine()) != null) {
					outputString = outputString + endingString + "\n";
				}
				
				writer.print(outputString);
				writer.flush();
				response.sendRedirect("http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_view_feedbacks.html");
				
			} 

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (queryfeedbacks != null) queryfeedbacks.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}