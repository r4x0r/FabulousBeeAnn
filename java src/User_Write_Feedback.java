import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_Write_Feedback extends HttpServlet {  // JDK 6 and above only
	int breakline = 35;
	int offset = 28;

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement queryfeedback = null;
		String queryStr = null;
		String dataString = "";

		String error = "";
		boolean exists = false;

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

		//for file IO
		BufferedReader reader;
		PrintWriter writer;

		try {
			if (loginName.isEmpty()) {
				error = "You are not authorized to view this page. Please proceed to login.";
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_login.html\";");
				out.println("</script></body></html>");
			} else {
				// Step 1: Allocate a database Connection object
				conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!
				// database-URL(hostname, port, default database), username, password

				// Step 2: Allocate a Statement object within the Connection
				queryfeedback = conn.createStatement();

				// check if user already reached the feedback limit
				queryStr = "SELECT * FROM Feedbacks WHERE book_id = '" + request.getParameter("ISBN") +
						"' AND user_id = '" + loginName + "';"; //need cookie
				ResultSet searchResult = queryfeedback.executeQuery(queryStr);
				if (searchResult.next()) {
					exists = true;
				}

				if (exists) {
					error = "You have already commented on this book! You are only allowed to comment on each book once.";

					out.println("<html><body><script type=\"text/javascript\">");  
					out.println("alert('" + error + "');"); 
					out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_feedback.html\";");
					out.println("</script></body></html>");

				} else {

					String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
					reader = new BufferedReader(new FileReader(filepathString + "user_write_feedback_template.html"));
					writer = new PrintWriter(filepathString + "user_write_feedback.html");

					int i;
					String outputString = "";
					for (i = 0; i < breakline; i ++) {
						outputString = outputString + reader.readLine() + "\n";
					}

					outputString = outputString + "                <input type=\"hidden\" name=\"ISBN\" value=\"" + request.getParameter("ISBN") + "\">\n";

					for (i = 0; i < offset; i ++) {
						outputString = outputString + reader.readLine() + "\n";
					}

					queryStr = "select * from Books where ISBN = ";
					queryStr = queryStr + "'" + request.getParameter("ISBN") + "';";

					// Step 3: Execute a SQL SELECT query
					searchResult = queryfeedback.executeQuery(queryStr);
					// 7 indent for tr, 8 indent for td (each indent = 2 whitespace)
					if (searchResult.next()) {
						dataString = dataString + "              <tr>";
						dataString = dataString + "\n                <td>" + searchResult.getString("ISBN");
						dataString = dataString + "\n                <td>" + searchResult.getString("title");
						dataString = dataString + "\n                <td>" + searchResult.getString("authors");
						dataString = dataString + "\n                <td>" + searchResult.getString("publisher");
						dataString = dataString + "\n                <td>" + searchResult.getInt("year_of_pub");
						dataString = dataString + "\n                <td>" + searchResult.getInt("copies_avail");
						dataString = dataString + "\n                <td>" + searchResult.getFloat("price");
						dataString = dataString + "\n                <td>" + searchResult.getString("format");
						dataString = dataString + "\n                <td>" + searchResult.getString("keywords");
						dataString = dataString + "\n                <td>" + searchResult.getString("subject");
						dataString = dataString + "\n              <tr>\n";
					}

					outputString = outputString + dataString;

					String endingString;
					while ((endingString = reader.readLine()) != null) {
						outputString = outputString + endingString + "\n";
					}

					writer.print(outputString);
					writer.flush();

					response.sendRedirect("http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_write_feedback.html");
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (queryfeedback != null) queryfeedback.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}