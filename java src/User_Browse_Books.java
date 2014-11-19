import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_Browse_Books extends HttpServlet {  // JDK 6 and above only
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
		Statement querybooks = null;
		String queryStr = null
		int params = 0

		try {
			// Step 1: Allocate a database Connection object
			conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!
			// database-URL(hostname, port, default database), username, password

			// Step 2: Allocate a Statement object within the Connection
			querybooks = conn.createStatement();

			// Step 2.1: set up query statement
			queryStr = "select * from Books where "
			// title check
			if (Global.checks(request.getParameter("title"), "a+p+w")) {
				queryStr = queryStr + "title = '" + request.getParameter("title") + "' ";
				params ++;
			}
			// authors check
			if (Global.checks(request.getParameter("authors"), "a+p+w")) {
				if (params > 0) {
					queryStr = queryStr + "and ";
				}
				queryStr = queryStr + "authors = '" + request.getParameter("authors") + "' ";
				params ++;
			}
			// publisher check
			if (Global.checks(request.getParameter("publisher"), "a+p+w")) {
				if (params > 0) {
					queryStr = queryStr + "and ";
				}
				queryStr = queryStr + "publisher = '" + request.getParameter("publisher") + "' ";
				params ++;
			}
			// subject check
			if (Global.checks(request.getParameter("subject"), "a+p+w")) {
				if (params > 0) {
					queryStr = queryStr + "and ";
				}
				queryStr = queryStr + "subject = '" + request.getParameter("subject") + "' ";
				params ++;
			}
			// isbn check
			if (Global.checks(request.getParameter("ISBN"), "n")) {
				if (params > 0) {
					queryStr = queryStr + "and ";
				}
				queryStr = queryStr + "ISBN = '" + request.getParameter("ISBN") + "' ";
				params ++;
			}
			// keywords check
			if (Global.checks(request.getParameter("keywords"), "a+p+w")) {
				if (params > 0) {
					queryStr = queryStr + "and ";
				}
				queryStr = queryStr + "keywords = '" + request.getParameter("keywords") + "' ";
				params ++;
			}
			

			if (params > 0) {
				//search is valid as there are at least some stuff to be searching
				//now determines if it is order by year_of_pub or avg_score
				queryStr = queryStr + "order by year_of_pub;"
			} else {
				//if search is invalid, abandon it
			}


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