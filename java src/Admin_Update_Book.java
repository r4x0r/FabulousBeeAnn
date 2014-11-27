import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Admin_Update_Book extends HttpServlet {  // JDK 6 and above only
	int breakline = 42;
	int db_copies = 0;
	
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
		Statement stmt = null;
		Statement checkDatabase = null;
		boolean insert_now = false;
		String error = "";

		try {
			// Allocate a database Connection object
			// Import global variables (url, db_username, db_pw) from the Global Class
			conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!

			// Allocate a Statement object within the Connection
			stmt = conn.createStatement();
			checkDatabase = conn.createStatement();

			// Perform checks on all user inputs
			if (!Global.checks(request.getParameter("ISBN"), "n")) {
				error = Global.error("ISBN", "invalid input");
			}
			else if (!Global.checks(request.getParameter("copies"), "n")) {
				error = Global.error("Number of Copies", "invalid input");
			}
			else {
				// Check whether ISBN exists within Database
				String bookcheckStr = "select ISBN, copies_avail from Books where ISBN = '" + request.getParameter("ISBN") + "';"; 
				ResultSet checkResult = checkDatabase.executeQuery(bookcheckStr);
				if (checkResult.next()) {
					db_copies = checkResult.getInt("copies_avail");
					if ((db_copies + Integer.parseInt(request.getParameter("copies")) > 0)) {
						insert_now = true;
					}
					else error = "You only have " + db_copies + " of this book!";
				}
				error = "No such book in FabulousBeeAnn Bookstore!";
			}

			if (insert_now) {
				// Perform update on Books table
				String sqlStr = "UPDATE Books " +
								"SET copies_avail = copies_avail + " + Integer.parseInt(request.getParameter("copies")) + " " +
								"WHERE ISBN = '" + request.getParameter("ISBN") + "';";

				stmt.executeUpdate(sqlStr);  // Send the query to the server
				
				String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
				reader = new BufferedReader(new FileReader(filepathString + "admin_update_book_template.html"));
				writer = new PrintWriter(filepathString + "admin_update_book_success.html");

				int i;
				String outputString = "";
				for (i = 0; i < breakline; i ++) {
					outputString = outputString + reader.readLine() + "\n";
				}
				
				String sqlStr1 = "select * from Books where ISBN = '" + request.getParameter("ISBN") + "';"; 
				
				ResultSet searchResult = stmt.executeQuery(sqlStr1);
				String dataString = "";
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
				response.sendRedirect("http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/admin_update_book_success.html");
			}
			else {
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/admin_update_book.html\";");
				out.println("</script></body></html>");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (checkDatabase != null) checkDatabase.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}