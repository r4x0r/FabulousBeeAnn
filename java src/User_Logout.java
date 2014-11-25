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
			Cookie [] cookies = null; 
			cookies = request.getCookies();
			if (cookies != null){
				System.out.println("Came in here");
				for (int i = 0; i < cookies.length; i ++){
					if (cookies[i].getName().equals("login")){
						cookies[i].setMaxAge(0);
						response.addCookie(cookies[i]);
						successful = true;
						break;
					}
				}
			}
			if (successful) {
				// Direct successful registration to success.html
				out.println("<html><body><script type=\"text/javascript\">");  
				out.println("alert('You have successfully logged out. We hope to see you again!');"); 
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
			error = "Logout failed! Please try again.";
			// Alerts User of error and redirects unsuccessful registration back to register.html
			out.println("<html><body><script type=\"text/javascript\">");  
			out.println("alert('" + error + "');"); 
			out.println("location = \"http://" + Global.getIPadd() + ":9999/FabulousBeeAnn" + "/user_account.html\";");
			out.println("</script></body></html>");
		} finally {
			out.close();  // Close the output writer
		}
	}
}
		