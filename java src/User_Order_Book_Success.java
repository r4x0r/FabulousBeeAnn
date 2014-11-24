import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_Order_Book_Success extends HttpServlet {  // JDK 6 and above only
	int breakline = 42;

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement orderbooks = null;
		String iqueryStr = null;
		String oqueryStr = null;
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
			orderbooks = conn.createStatement();

			// check positive number of copies
			int num_copies = Integer.parseInt(request.getParameter("copies"));
			if (num_copies > 0) {
				
				// retrieve the next order number
				iqueryStr = "SELECT MAX(order_id) + 1 AS next_order_id FROM Orders;";
				ResultSet searchResult = orderbooks.executeQuery(iqueryStr);
				int order_num = 0;
				if (searchResult.next()) {
					order_num = searchResult.getInt("next_order_id");
				}
				
				// get current date
				Date today = new Date(System.currentTimeMillis());
				
				// insert into book order into Orders table
				iqueryStr = "INSERT INTO Orders VALUES ('" + 
							 "job" + "', '" + 
							 request.getParameter("ISBN") + "', " +
							 order_num + ", " +
							 num_copies + ", '" +
							 today + 
							 "', 'Pending');";
				orderbooks.executeUpdate(iqueryStr);
				
				// subtract from Books
				iqueryStr = "UPDATE Books SET copies_avail = copies_avail - " + num_copies + " WHERE ISBN = '" + request.getParameter("ISBN") + "';";
				orderbooks.executeUpdate(iqueryStr);
				
				exists = true;
			}

			if (exists) {
//				SELECT ISBN, title, authors, publisher, year_of_pub, copies_avail, price, format, keywords, subject, sales
//				FROM Books JOIN (SELECT book_id, SUM(copies) AS sales
//								 FROM Orders
//								 WHERE book_id <> '978-0486411217'
//								 AND user_id IN (SELECT user_id
//												   FROM Orders
//												   WHERE book_id = '978-0486411217'
//												   AND user_id <> 'job')
//								GROUP BY book_id) recommend
//				ON ISBN = book_id
//				ORDER BY sales DESC;
				
				String filepathString = "/Users/tanchingyi93/Google Drive/apache-tomcat-7.0.56/webapps/FabulousBeeAnn/";
				reader = new BufferedReader(new FileReader(filepathString + "user_recommend_books_template.html"));
				writer = new PrintWriter(filepathString + "user_recommend_books.html");

				int i;
				String outputString = "";
				for (i = 0; i < breakline; i ++) {
					outputString = outputString + reader.readLine() + "\n";
				}
				
				String sqlStr1 = "SELECT ISBN, title, authors, publisher, year_of_pub, copies_avail, price, format, keywords, subject, sales " +
									"FROM Books JOIN (SELECT book_id, SUM(copies) AS sales " +
														"FROM Orders " +
														"WHERE book_id <> '" + request.getParameter("ISBN") + "' " +
														"AND user_id IN (SELECT user_id " +
																		"FROM Orders " +
																		"WHERE book_id = '" + request.getParameter("ISBN") + "' " +
																		"AND user_id <> 'job') " +
														"GROUP BY book_id) recommend " +
									"ON ISBN = book_id " +
									"ORDER BY sales DESC;";
				
				ResultSet searchResult = orderbooks.executeQuery(sqlStr1);
				while (searchResult.next()) {
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
					dataString = dataString + "\n                <td>" + searchResult.getInt("sales");
					dataString = dataString + "\n              <tr>\n";
				}
				outputString = outputString + dataString;
				
				String endingString;
				while ((endingString = reader.readLine()) != null) {
					outputString = outputString + endingString + "\n";
				}
				
				writer.print(outputString);
				writer.flush();
				
				error = "Thank You! Your order is successful! You can keep track of your orders from My Orders page";
				
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_recommend_books.html\";");
				out.println("</script></body></html>");

			} else {
				error = "Something is wrong with your order. Please try again later.";

				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_order_book.html\";");
				out.println("</script></body></html>");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (orderbooks != null) orderbooks.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}