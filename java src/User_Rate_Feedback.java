import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_Rate_Feedback extends HttpServlet {  // JDK 6 and above only
	int breakline = 42;

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement queryrate = null;
		String queryStr = null;
		String iqueryStr = null;
		
		String error = "";
		boolean exists = false;

		String loginName = "";
		Cookie [] cookies = null;
		cookies = request.getCookies();
		if (cookies != null){
			for (int i = 0; i < cookies.length; i++){
				if(cookies[i].getName().equals("login")){
					loginName = cookies[i].getValue();
				}

			}
		}	

		try {
			if (loginName.isEmpty()) {
				error = "Rating not submitted! You are not authorized to view this page. Please proceed to login.";
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_login.html\";");
				out.println("</script></body></html>");
			}
			else { 
				// Step 1: Allocate a database Connection object
				conn = DriverManager.getConnection(Global.getMySQLconn(), Global.getSQLuser(), Global.getSQLpwd()); // <== Check!
				// database-URL(hostname, port, default database), username, password

				// Step 2: Allocate a Statement object within the Connection
				queryrate = conn.createStatement();

				// check for validity
				String isbn = request.getParameter("ISBN");
				String f_id = request.getParameter("feedback_id");
				queryStr = "select * from Feedbacks where book_id = '" +
						   isbn + "' and user_id = '" +
						   f_id + "';";
				System.out.println(queryStr);
				ResultSet searchResult = queryrate.executeQuery(queryStr);
				
				if (searchResult.next()) {
					// feedback exists, feedback_id is valid
					
					// check if user is rating own comment
					if (loginName.equals(f_id)) {
						error = "Sorry. You cannot rate your own feedback.";
					} else {
						
						// check if user had already rated on this feedback
						queryStr = "select * from Likes where book_id = '" +
						           isbn + "' and commenter_id = '" +
						           f_id + "' and liker_id = '" + 
						           loginName + "';";
						System.out.println(queryStr);
						searchResult = queryrate.executeQuery(queryStr);
						
						if (searchResult.next()) {
							error = "Sorry. You have already rated this feedback. You are allowed to rate each feedback only once.";
						} else {
							// good to go
							exists = true;
						}
					}
				} else {
					// feedback do not exists
					error = "Sorry. Invalid feedback id. Please refine your input.";
				}
				System.out.println("after validation error: " + error);
				System.out.println("exists: " + exists);
				
				// find out the rating selected
				int rate = -1;
				String rating = request.getParameter("rating");
				System.out.println("rating string: " + rating);
				if (rating != null) {
					if (rating.equals("useless")) {
						rate = 0;
						iqueryStr = "UPDATE Feedbacks SET useless = useless + 1 WHERE book_id = '" + isbn + "' AND user_id = '" + f_id + "';";
					} else if (rating.equals("useful")) {
						rate = 1;
						iqueryStr = "UPDATE Feedbacks SET useful = useful + 1 WHERE book_id = '" + isbn + "' AND user_id = '" + f_id + "';";
					} else if (rating.equals("very_useful")) {
						rate = 2;
						iqueryStr = "UPDATE Feedbacks SET very_useful = very_useful + 1 WHERE book_id = '" + isbn + "' AND user_id = '" + f_id + "';";
					}
				}
				if (rate == -1) {
					exists = false;
					error = "Sorry. Please select one of the rating options and try again.";
				}
				System.out.println("after rate determination: " + error);
				System.out.println("exists: " + exists);
				
				if (exists) {
					// record into Likes table
					queryStr = "INSERT INTO Likes VALUES ('" + 
							request.getParameter("ISBN") + "', '" +
							f_id + "', '" +
							loginName + "', " +
							rate + ");";
					System.out.println(queryStr);
					queryrate.executeUpdate(queryStr);
					
					// record into feedback tuple
					System.out.println(iqueryStr);
					queryrate.executeUpdate(iqueryStr);
					
					error = "Thank You! Your rating had been successfully recorded. You can review feedbacks you had rated from My Ratings.";
					out.println("<html><body><script type=\"text/javascript\">");  
					out.println("alert('" + error + "');"); 
					out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_home.html\";");
					out.println("</script></body></html>");
					
				} else {
					out.println("<html><body><script type=\"text/javascript\">");  
					out.println("alert('" + error + "');"); 
					out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_view_feedbacks.html\";");
					out.println("</script></body></html>");
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (queryrate != null) queryrate.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}