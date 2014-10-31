

import java.util.regex.Pattern;

public class Global {
	private static String IPadd = "192.168.1.110";
	private static String MySQLconn = "jdbc:mysql://" + IPadd + "/Bookshop";
	private static String SQLuser = "manager";
	private static String SQLpwd = "root";
	
	public static String getIPadd() {
		return IPadd;
	}
	
	public static String getMySQLconn() {
		return MySQLconn;
	}
	
	public static String getSQLuser() {
		return SQLuser;
	}
	
	public static String getSQLpwd() {
		return SQLpwd;
	}
	
	/**
	 * a: alphanumeric
	 * n: numeric
	 * p: punctuation
	 * w: whitespace
	 * 
	 * @param input
	 * @param type
	 * @return
	 */
	public static boolean checks(String input, String type) {
		boolean result = false;
		
		switch(type) {
		case "a":
			result = Pattern.matches("[a-zA-Z]+", input);
			break;
		case "n":
			result = Pattern.matches("[0-9]+", input); //at least 8 numbers
			break;
		case "a+n":
			result = Pattern.matches("[a-zA-Z0-9]+", input);
			break;
		case "a+n+w":
			result = Pattern.matches("[a-zA-Z0-9 ]+", input);
			break;
		case "a+p+w":
			result = Pattern.matches("[a-zA-Z ']+", input);
			break;
		case "a+n+p+w":
			result = Pattern.matches("[a-zA-Z0-9 #\\-]+", input);
			break;
		}
		return result;
	}
	
	public static String error(String input, String type) {
		String result = "Error detected";
		switch (type) {
		case "invalid input":
			result = "Invalid " + input + " input. Please retype.";
			break;
		}
		return result;
	}
}
