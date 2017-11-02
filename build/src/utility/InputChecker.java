package utility;

public class InputChecker {
	/**
	 * Simple helper method to check if a given string is a valid port number
	 * @param str The port String we are checking
	 * @return Whether this is a valid port number
	 */
	public static boolean isValidPortNumber(String str){
		if(!str.matches("(0|[1-9][0-9]*)")) {
			return false; 
		}
		try {
			int num = Integer.parseInt(str); 
			if(num > 65535 || num < 1024) {
				return false; 
			}
		}catch(Exception e) {
			return false; 
		}
		return true; 
	}

	
	/**
	 * Simple helper method to check if a given string is a valid auth file. 
	 * @param str The auth file String we are checking
	 * @return Whether this is a valid filename
	 */
	public static boolean isValidAuthFile(String str){
		if(!str.matches("[_\\-\\.0-9a-z]+")) {
			//System.out.println("Not matching regex.");
			return false; 
		}
		if(str.equals(".") || str.equals("..")) {
			//System.out.println("No . or ..");
			return false; 
		}
		if(str.length() > 255) {
			//System.out.println("Too large.");
			return false; 
		}
		return true; 
	}
}
