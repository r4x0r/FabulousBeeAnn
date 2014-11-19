import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_Login extends HttpServlet {  // JDK 6 and above only
	String error = "";
	boolean exists = false;

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement checkDatabase = null;
		try {
			// Step 1: Allocate a database Connection object
			conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!
			// database-URL(hostname, port, default database), username, password

			// Step 2: Allocate a Statement object within the Connection
			checkDatabase = conn.createStatement();

			// Perform checks on all user inputs
			if (Global.checks(request.getParameter("login"), "a+n")) {
				// Check whether login name exists within Database
				String logincheckStr = "select login, password from Customers where login = '" + request.getParameter("login") + "';"; 
				ResultSet checkResult = checkDatabase.executeQuery(logincheckStr);
				if (checkResult.next()) {
					if (request.getParameter("pw").equals(checkResult.getString("password"))) {
						exists = true;
					}
				}
			}

			// Step 3: Execute a SQL SELECT query

			if (exists) {
				// Direct successful registration to success.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('Success');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_home.html\";");
				out.println("</script></body></html>");
			}
			else {
				error = "Invalid login details! Please try again.";
				// Alerts User of error and redirects unsuccessful registration back to register.html
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