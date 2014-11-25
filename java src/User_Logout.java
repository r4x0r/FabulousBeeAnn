import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class User_Logout extends HttpServlet {  // JDK 6 and above only
	String error = "";

	// The doGet() runs once per HTTP GET request to this servlet.
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set the MIME type for the response message
		response.setContentType("text/html");
		// Get a output writer to write the response message into the network socket
		PrintWriter out = response.getWriter();

		boolean successful = false;
		try {

			/////////////////////////////////// Remove Cookie from Browser //////////////////////////////////////////
			

			if (successful) {
				// Direct successful registration to success.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('You have successfully logged out. Thank you for visiting FabulousBeeAnn! We hope to see you again!');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/\";");
				out.println("</script></body></html>");

			}
			else {
				error = "Logout failed! Please try again.";
				// Alerts User of error and redirects unsuccessful registration back to register.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('" + error + "');"); 
				out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_account.html\";");
				out.println("</script></body></html>");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			out.close();  // Close the output writer
		}
	}
}