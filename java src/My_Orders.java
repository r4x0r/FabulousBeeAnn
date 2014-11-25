import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class My_Orders extends HttpServlet {  // JDK 6 and above only
	int breakline = 37;
	
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
			if (false) { 
				error = "You do not have the permission to view this page. Please proceed to login.";
			}
			else {
				// Check whether ISBN exists within Database

				String queryStr = "SELECT order_id, book_id, copies, date, status " +
										"FROM Orders " +
										"WHERE user_id = 'job'"; // Change the user login from here <------

				ResultSet checkResult = checkDatabase.executeQuery(queryStr);
				if (checkResult.next()) {
					dataString = dataString + "              <tr>";
					dataString = dataString + "\n                <td>" + checkResult.getLong("order_id");
					dataString = dataString + "\n                <td>" + checkResult.getString("book_id");
					dataString = dataString + "\n                <td>" + checkResult.getInt("copies");
					dataString = dataString + "\n                <td>" + checkResult.getDate("date");
					dataString = dataString + "\n                <td>" + checkResult.getString("status");
					dataString = dataString + "\n              <tr>\n";
				}
				insert_now = true;
			}

			if (insert_now) {
				String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
				reader = new BufferedReader(new FileReader(filepathString + "my_orders_template.html"));
				writer = new PrintWriter(filepathString + "my_orders.html");

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
				response.sendRedirect("http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/my_orders.html");
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