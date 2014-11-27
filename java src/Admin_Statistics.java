import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Admin_Statistics extends HttpServlet {  // JDK 6 and above only
	int breakline = 33;

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement queryStats = null;
		String queryStr = null;
		String dataString = "";
		String error = "";
		boolean allow_query = false;
		int m = 0;

		String last_date = "";
		String dateBetween = "";


		//for file IO
		BufferedReader reader;
		PrintWriter writer;

		try {
			// Step 1: Allocate a database Connection object
			conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!
			// database-URL(hostname, port, default database), username, password

			// Step 2: Allocate a Statement object within the Connection
			queryStats = conn.createStatement();

			// Step 2.1: set up query statement			
			// Changed to show all 5 columns to follow the table.
			queryStr = "";

			// Checking date
			Date d = new Date(System.currentTimeMillis());
			String curr_date = String.valueOf(d);
			String curr_month = (curr_date.substring(5, 7));
			String curr_year = (curr_date.substring(0, 4));

			String[] long_months = {"1", "3", "5", "7", "8", "10", "12"};
			HashSet<String> long_months_set = new HashSet<>();
			for (int mth = 0; mth < long_months.length; mth++) {
				long_months_set.add(long_months[mth]);
			}

			if (long_months_set.contains(curr_month)) { 
				last_date = "31";
			}
			else if (curr_month.equalsIgnoreCase("2")) { // February
				last_date = "28";
			}	
			else { 
				last_date = "30";
			}

			String date = (curr_year + "-" + curr_month + "-" + last_date);
			String start_date = (curr_year + "-" + curr_month + "-" + "01");
			dateBetween = "WHERE date BETWEEN " + "'" + start_date +"'" +
					" AND " + "'" + date + "'" ;

			// m_number check
			if (!Global.checks(request.getParameter("m_number"), "n")) {
				error = Global.error("m_number", "invalid input");
			}
			else if (Integer.parseInt(request.getParameter("m_number")) > 0) {
				allow_query = true;
				m = Integer.parseInt(request.getParameter("m_number"));
			}

			if (allow_query) {
				Thread.sleep(1000);
				// radio button check			
				if (request.getParameter("option").equalsIgnoreCase("best_books")) {
					// Best-Selling Books
					queryStr = queryStr + "SELECT ISBN, title, authors, publisher, sold_this_mth " + 
											"FROM Books JOIN (SELECT book_id, SUM(copies) AS sold_this_mth " +
																"FROM Orders " + 
																dateBetween + 
																" GROUP BY book_id) Sale " +
											"ON ISBN = book_id "+
											"ORDER BY sold_this_mth DESC;";
					ResultSet searchResult = queryStats.executeQuery(queryStr);
					
					dataString = dataString + 	"            <thead>\n" +
												"              <tr>\n" +
												"                <th width=\"200\">ISBN</th>\n" +
												"                <th width=\"400\">Title</th>\n" +
												"                <th width=\"200\">Author</th>\n" +
												"                <th width=\"300\">Publisher</th>\n" +
												"                <th width=\"350\">Copies Sold This Month</th>\n" +
												"              </tr>\n" +
												"            </thead>\n" +
												"            <tbody>\n";
					
					while (searchResult.next() & (m > 0)) {
						dataString = dataString + "                <tr>";
						dataString = dataString + "\n                  <td>" + searchResult.getString("ISBN");
						dataString = dataString + "\n                  <td>" + searchResult.getString("title");
						dataString = dataString + "\n                  <td>" + searchResult.getString("authors");
						dataString = dataString + "\n                  <td>" + searchResult.getString("publisher");
						dataString = dataString + "\n                  <td>" + searchResult.getInt("sold_this_mth");
						dataString = dataString + "\n                <tr>\n";
						m--;
					}
				}

				else if (request.getParameter("option").equalsIgnoreCase("best_authors")) {
					// Best-Selling Authors
					queryStr = queryStr + "SELECT authors, SUM(copies) AS sold_this_mth " + 
							"FROM Books JOIN (SELECT book_id, copies " +
											"FROM Orders " + 
											dateBetween + ") Sale " + 
							"ON ISBN = book_id "+
							"GROUP BY authors "+
							"ORDER BY sold_this_mth DESC;";
					ResultSet searchResult = queryStats.executeQuery(queryStr); 
					
					dataString = dataString + 	"            <thead>\n" +
												"              <tr>\n" +
												"                <th width=\"200\">Author</th>\n" +
												"                <th width=\"350\">Copies Sold This Month</th>\n" +
												"              </tr>\n" +
												"            </thead>\n" +
												"            <tbody>\n";
					
					while (searchResult.next() & (m > 0)) {
						dataString = dataString + "                <tr>";
						dataString = dataString + "\n                  <td>" + searchResult.getString("authors");
						dataString = dataString + "\n                  <td>" + searchResult.getInt("sold_this_mth");
						dataString = dataString + "\n                <tr>\n";
						m--;
					}
				}

				else if (request.getParameter("option").equalsIgnoreCase("best_publishers")) {
					// Best-Selling Publisher
					queryStr = queryStr + "SELECT publisher, SUM(copies) AS sold_this_mth " + 
							"FROM Books JOIN (SELECT book_id, copies " +
												"FROM Orders " + 
												dateBetween + ") Sale " +
							"ON ISBN = book_id "+
							"GROUP BY publisher "+
							"ORDER BY sold_this_mth DESC;";
					ResultSet searchResult = queryStats.executeQuery(queryStr); 

					dataString = dataString + 	"            <thead>\n" +
												"              <tr>\n" +
												"                <th width=\"300\">Publisher</th>\n" +
												"                <th width=\"350\">Copies Sold This Month</th>\n" +
												"              </tr>\n" +
												"            </thead>\n" +
												"            <tbody>\n";
					
					while (searchResult.next() & (m > 0)) {
						dataString = dataString + "                <tr>";
						dataString = dataString + "\n                  <td>" + searchResult.getString("publisher");
						dataString = dataString + "\n                  <td>" + searchResult.getInt("sold_this_mth");
						dataString = dataString + "\n                <tr>\n";
						m--;
					}
				}

				//query from database
				String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
				reader = new BufferedReader(new FileReader(filepathString + "admin_statistics_template.html"));
				writer = new PrintWriter(filepathString + "admin_statistics_results.html");

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
				allow_query = false;
				response.sendRedirect("http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/admin_statistics_results.html");
			}

			else {
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/admin_statistics.html\";");
				out.println("</script></body></html>");
			}

		} catch (SQLException | InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (queryStats != null) queryStats.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}