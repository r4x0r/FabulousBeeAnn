import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_Write_Feedback_Success extends HttpServlet {  // JDK 6 and above only

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		Connection conn = null;
		Statement queryfeedbacks = null;
		String iqueryStr = null;
		
		String error = "";

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
				error = "Feedback not recorded! You are not authorized to view this page. Please proceed to login and try again!";
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
				queryfeedbacks = conn.createStatement();

				// check if score is valid
				int score = Integer.parseInt(request.getParameter("score"));
				if ((score > -1) & (score < 11)) {
					
					// get current date
					Date today = new Date(System.currentTimeMillis());

					// insert into feedback table
					iqueryStr = "INSERT INTO Feedbacks VALUES ('" + 
							request.getParameter("ISBN") + "', '" + 
							loginName + "', " + 
							score + ", '" +
							request.getParameter("comment") + "', '" +
							today + 
							"', 0, 0, 0);";
					System.out.println(iqueryStr);
					queryfeedbacks.executeUpdate(iqueryStr);
					
					error = "Thank You! Your feedback has been successfully recorded! You can check your feedback records in My Feedbacks";

					out.println("<html><body><script type=\"text/javascript\">");  
					out.println("alert('" + error + "');"); 
					out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_home.html\";");
					out.println("</script></body></html>");
					
				} else {
					error = "You have submitted an invalid score. Please try again";

					out.println("<html><body><script type=\"text/javascript\">");  
					out.println("alert('" + error + "');"); 
					out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_write_feedback.html\";");
					out.println("</script></body></html>");
				}
			}
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
			try {
				// Step 5: Close the resources
				if (queryfeedbacks != null) queryfeedbacks.close();
				if (conn != null) conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}