import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_Order_Book extends HttpServlet {  // JDK 6 and above only
	int breakline = 33;
	int offset = 29; 

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement querybooks = null;
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
			querybooks = conn.createStatement();

			// Step 2.1: set up query statement
			queryStr = "select * from Books where ISBN = ";
			// isbn check
			if (Global.checks(request.getParameter("ISBN"), "n")) {
				queryStr = queryStr + "'" + request.getParameter("ISBN") + "';";

				// Step 3: Execute a SQL SELECT query
				ResultSet searchResult = querybooks.executeQuery(queryStr);
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

					exists = true;

				}
			}

			if (exists) {
				String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
				reader = new BufferedReader(new FileReader(filepathString + "user_order_book_template.html"));
				writer = new PrintWriter(filepathString + "user_order_book.html");

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
				response.sendRedirect("http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_order_book.html");

			} else {
				// if search is invalid, abandon it
				error = "Invalid ISBN input. Please refine your input and try again.";
				// Alerts User of error and redirects unsuccessful registration back to register.html
				response.setIntHeader("Refresh", 5);
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_browse_books_results.html\";");
				out.println("</script></body></html>");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (querybooks != null) querybooks.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}