package bank_server;

//Package for parsing command line arguments
import org.apache.commons.cli.*; 

public class Main {
	
	
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

	
	public static void main(String[] args) {
		
		//Add options: 
		Options options = new Options(); 
		options.addOption("p", true, "The port for this server to run on"); 
		options.addOption("s", true, "The authorization file"); 
		CommandLineParser parser = new DefaultParser(); 
		
		int port = 3000; 
		String authFile = "bank.auth"; 
		
		try {
			CommandLine cmd = parser.parse(options, args);
			
			//Check for port option
			if(cmd.hasOption("p")) {
				String portString = cmd.getOptionValue("p"); 
				if(isValidPortNumber(portString)) {
					port = Integer.parseInt(portString); 
				}else {
					System.out.println("Invalid port number."); 
				}
			}
			
			//Check for auth file
			//TODO: Add check for valid auth file. 
			if(cmd.hasOption("s")) {
				authFile = cmd.getOptionValue("s"); 
			} 
			
		} catch(Exception e) {
			e.printStackTrace(); 
			System.out.println("wrong args fam.");
		}
	}

}
