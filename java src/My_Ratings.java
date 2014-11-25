import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class My_Ratings extends HttpServlet {  // JDK 6 and above only
	int breakline = 39;
	
	//for file IO
	BufferedReader reader;
	PrintWriter writer;

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement checkDatabase = null;
		boolean insert_now = false;
		String error = "";
		String dataString = "";

		try {
			// Step 1: Allocate a database Connection object
			conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!
			// database-URL(hostname, port, default database), username, password

			// Step 2: Allocate a Statement object within the Connection
			checkDatabase = conn.createStatement();
			
			// Perform checks on Cookie for user login
			String loginName = "";
			Cookie [] cookies = null;
			cookies = request.getCookies();
			if (cookies != null){
				for (int i = 0; i < cookies.length; i++){
					if(cookies[i].getName().equals("login")){
						loginName = cookies[i].getValue();
					}
				}
			}

			if (loginName.isEmpty()) { 
				error = "You do not have the permission to view this page. Please proceed to login.";
			}
			else {
				// Check whether ISBN exists within Database

				String queryStr = "SELECT f.book_id, f.user_id, f.score, f.comment, f.date, (f.useful + (f.very_useful*2.0)) AS total_ratings, (f.useless + f.useful + f.very_useful) AS num_ppl_rated, l.rating " +
									"FROM Feedbacks f, (SELECT book_id, commenter_id, rating " +
														"FROM Likes " +
														"WHERE liker_id = '" + loginName + "') l " + 
									"WHERE f.book_id = l.book_id " +
									"AND f.user_id = l.commenter_id;"; 

				ResultSet checkResult = checkDatabase.executeQuery(queryStr);
				while (checkResult.next()) {
					dataString = dataString + "              <tr>";
					dataString = dataString + "\n                <td>" + checkResult.getString("book_id");
					dataString = dataString + "\n                <td>" + checkResult.getString("user_id");
					dataString = dataString + "\n                <td>" + checkResult.getInt("score");
					dataString = dataString + "\n                <td>" + checkResult.getString("comment");
					dataString = dataString + "\n                <td>" + checkResult.getDate("date");
					
					int num_of_raters = checkResult.getInt("num_ppl_rated");
					if (num_of_raters == 0) {
						dataString = dataString + "\n                <td>" + checkResult.getFloat("total_ratings");
					}
					else {
						dataString = dataString + "\n                <td>" + (checkResult.getFloat("total_ratings")/num_of_raters);
					} 
					dataString = dataString + "\n                <td>" + checkResult.getInt("rating");
					dataString = dataString + "\n              <tr>\n";
				}
				insert_now = true;
			}

			if (insert_now) {
				String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
				reader = new BufferedReader(new FileReader(filepathString + "my_ratings_template.html"));
				writer = new PrintWriter(filepathString + "my_ratings.html");

				int i;
				String outputString = "";
				for (i = 0; i < breakline; i ++) {
					outputString = outputString + reader.readLine() + "\n";
				}

				outputString = outputString + dataString;
				
				String endingString;
				while ((endingString = reader.readLine()) != null) {
					outputString = outputString + endingString + "\n";
				}
				
				writer.print(outputString);
				writer.flush();
				response.sendRedirect("http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/my_ratings.html");
			}
			else {
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_login.html\";");
				out.println("</script></body></html>");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (checkDatabase != null) checkDatabase.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}