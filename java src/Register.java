import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Register extends HttpServlet {  // JDK 6 and above only
	String error = "";
	boolean insert_now = false;

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			if (!Global.checks(request.getParameter("name"), "a+p+w")) {
				error = Global.error("Full Name", "invalid input");
			}
			else if (!Global.checks(request.getParameter("login"), "a+n")) {
				error = Global.error("Login Name", "invalid input");
			}
			else if (!Global.checks(request.getParameter("pw"), "a+n")) {
				error = Global.error("Password", "invalid input");
			}
			else if (!Global.checks(request.getParameter("pw2"), "a+n")) {
				error = "Passwords do not match";
			}
			else if (!Global.checks(request.getParameter("address"), "a+n+p+w")) {
				error = Global.error("Address", "invalid input");
			}
			else if (!Global.checks(request.getParameter("creditcard"), "n")) {
				error = Global.error("Credit Card Number", "invalid input");
			}
			else if (!Global.checks(request.getParameter("phone"), "n")) {
				error = Global.error("Contact Number", "invalid input");
			}
			else {
				// Check whether login name exists within Database
				boolean exists = true;
				String logincheckStr = "select login from Customers where login = '" + request.getParameter("login") + "';"; 
				ResultSet checkResult = checkDatabase.executeQuery(logincheckStr);
				if (!checkResult.next()) insert_now = true;
				if (exists) error = "Login Name is already taken. Please choose another Login Name.";

			}


			// Step 3: Execute a SQL SELECT query

			if (insert_now) {
				String sqlStr = "INSERT INTO Customers " +
						"VALUES ('" + request.getParameter("login") + "'," +
						"'" + request.getParameter("pw") + "'," +
						"'" + request.getParameter("name") + "'," +
						"'" + request.getParameter("creditcard") + "'," +
						"'" + request.getParameter("address") + "'," +
						"" + Long.parseLong(request.getParameter("phone")) + ");";

				stmt.executeUpdate(sqlStr);  // Send the query to the server

				// Direct successful registration to success.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('Success');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_register_success.html\";");
				out.println("</script></body></html>");
			}
			else {
				// Alerts User of error and redirects unsuccessful registration back to register.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_register.html\";");
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