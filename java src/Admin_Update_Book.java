import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Admin_Update_Book extends HttpServlet {  // JDK 6 and above only
	String error = "";
	boolean insert_now = false;
	int db_copies = 0;

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
		try {
			// Step 1: Allocate a database Connection object
			conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!
			// database-URL(hostname, port, default database), username, password

			// Step 2: Allocate a Statement object within the Connection
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
				// Step 3: Execute a SQL SELECT query

				String sqlStr = "UPDATE Books " +
								"SET copies_avail = copies_avail + " + Integer.parseInt(request.getParameter("copies")) + " " +
								"WHERE ISBN = '" + request.getParameter("ISBN") + "';";

				stmt.executeUpdate(sqlStr);  // Send the query to the server

				// Direct successful registration to success.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('Success');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/admin_update_book_success.html\";");
				out.println("</script></body></html>");
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