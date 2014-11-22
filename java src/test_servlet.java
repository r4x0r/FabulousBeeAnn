import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class test_servlet extends HttpServlet {  // JDK 6 and above only
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
		String queryStr = null;
		String asQueryStr = null;
		int params = 0;

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
			queryStr = "select * from Books where ";
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

			// Step 3: Execute a SQL SELECT query
			if (params > 0) {
				// search is valid as there are at least some stuff to be searching
				if (request.getParameter("sortby").equalsIgnoreCase("year")) {
					System.out.println("executed if year!!!!");

					queryStr = queryStr + "order by year_of_pub;";
					System.out.println(queryStr);
					ResultSet searchResult = querybooks.executeQuery(queryStr);

					try {
						String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
						reader = new BufferedReader(new FileReader(filepathString + "user_browse_books_template.html"));
						writer = new PrintWriter(filepathString + "user_browse_books_results.html");

						int i;
						String outputString = "";
						for (i = 0; i < 70; i ++) {
							outputString = outputString + reader.readLine() + "\n";
						}

						// 7 indent for tr, 8 indent for td (each indent = 2 whitespace)
						while (searchResult.next()) {
							String dataString = "              <tr>";
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
							dataString = dataString + "\n                <td>" + " ";
							dataString = dataString + "\n              <tr>\n";

							outputString = outputString + dataString;
						}

						String endingString;
						while ((endingString = reader.readLine()) != null) {
							outputString = outputString + endingString + "\n";
						}

						//System.out.print(outputString);
						writer.print(outputString);
						writer.flush();

					} catch (Exception e) {
						e.printStackTrace();
					}

					error = "Search complete";
					// Alerts User of error and redirects unsuccessful registration back to register.html
//					out.println("<html><body><script type=\"text/javascript\">");  
//					out.println("alert('" + error + "');"); 
//					out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_browse_results.html\";");
//					out.println("</script></body></html>");
					response.sendRedirect("http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_browse_books_results.html");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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