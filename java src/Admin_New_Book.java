import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Admin_New_Book extends HttpServlet {  // JDK 6 and above only
	String error = "";
	
	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement queryDatabase = null;
		boolean insert_now = false;
		
		try {
			// Allocate a database Connection object
			// Import global variables (url, db_username, db_pw) from the Global Class
			conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd());

			// Allocate a Statement object within the Connection
			queryDatabase = conn.createStatement();

			// Perform checks on all user inputs
			if (!Global.checks(request.getParameter("title"), "a+p+w")) {
				error = Global.error("Title", "invalid input");
			}
			else if (!Global.checks(request.getParameter("ISBN"), "n")) {
				error = Global.error("ISBN", "invalid input");
			}
			else if (!Global.checks(request.getParameter("author"), "a+p+w")) {
				error = Global.error("Author", "invalid input");
			}
			else if (!Global.checks(request.getParameter("pub"), "a+p+w")) {
				error = Global.error("Publisher", "invalid input");
			}
			else if (!Global.checks(request.getParameter("yr_of_pub"), "date")) {
				error = Global.error("Year of Publication", "invalid input");
			}
			else if (!Global.checks(request.getParameter("copies"), "n")) {
				error = Global.error("Number of Copies", "invalid input");
			}
			else if (!Global.checks(request.getParameter("price"), "f")) {
				error = Global.error("Price", "invalid input");
			}
			else if (!Global.checks(request.getParameter("format"), "format")) {
				error = Global.error("Format", "invalid input");
			}
			else if (!Global.checks(request.getParameter("keywords"), "a+p+w")) {
				error = Global.error("Keywords", "invalid input");
			}
			else if (!Global.checks(request.getParameter("subject"), "a+p+w")) {
				error = Global.error("Subject", "invalid input");
			}
			else {
				// Check whether ISBN exists within Database
				boolean exists = true;
				String bookcheckStr = "select ISBN from Books where ISBN = '" + request.getParameter("ISBN") + "';"; 
				ResultSet checkResult = queryDatabase.executeQuery(bookcheckStr);
				if (!checkResult.next()) {
					insert_now = true;
				}
				if (exists) error = "Book already exists.";

			}

			if (insert_now) {
				// Perform insertion into Books table
				String sqlStr = "INSERT INTO Books " +
						"VALUES ('" + request.getParameter("ISBN") + "'," +
						"'" + request.getParameter("title") + "'," +
						"'" + request.getParameter("author") + "'," +
						"'" + request.getParameter("pub") + "'," +
						"" + Integer.parseInt(request.getParameter("yr_of_pub")) + "," +
						"" + Integer.parseInt(request.getParameter("copies")) + "," +
						"" + Float.parseFloat(request.getParameter("price")) + "," +
						"'" + request.getParameter("format") + "'," +
						"'" + request.getParameter("keywords") + "'," +
						"'" + request.getParameter("subject") + "');";

				queryDatabase.executeUpdate(sqlStr);  // Send the query to the server

				// Direct successful registration to admin_new_book_success.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('Success');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/admin_new_book_success.html\";");
				out.println("</script></body></html>");
			}
			else {
				// Direct unsuccessful registration back to admin_new_book.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/admin_new_book.html\";");
				out.println("</script></body></html>");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Close the resources
				if (queryDatabase != null) queryDatabase.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}