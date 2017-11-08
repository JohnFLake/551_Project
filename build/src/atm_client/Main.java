package atm_client;

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
		options.addOption("a", true, "The customer's account name"); 
		options.addOption("s", true, "The authorization file"); 
		options.addOption("i", true, "The IP address for this server to run on"); 
		options.addOption("p", true, "The port for this server to run on"); 
		options.addOption("c", true, "The customer's ATM card file"); 
		options.addOption("n", false, "Create account with specified amount"); 
		options.addOption("d", false, "Deposit amount"); 
		options.addOption("w", false, "Withdrawl amount"); 
		options.addOption("g", false, "Check account amount"); 
		
		System.
		CommandLineParser parser = new DefaultParser(); 
		
		
		
		int port = 3000; 
		String IP = "127.0.0.1";
		String authFile = "bank.auth"; 
		String accountName;
		String cardFile;
		
		try {
			CommandLine cmd = parser.parse(options, args);
			
			// Check for account name
			if(cmd.hasOption("a")) {
				accountName = cmd.getOptionValue("a");
				cardFile = accountName + ".card";
			}
			
			else {
				System.out.println("Need an account name entered");
			}
			
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
			
			// Check for IP address
			if(cmd.hasOption("p")) {
				IP = cmd.getOptionValue("p"); 
			} 
			
			// Check for Card File
			if(cmd.hasOption("c")) {
				cardFile = cmd.getOptionValue("c") + ".card"; 
			} 
			
			
			
		} catch(Exception e) {
			e.printStackTrace(); 
			System.out.println("wrong args fam.");
		}
		
		Client newclient = new Client(authFile, IP, port, cardFile, accountName);
		System.out.println("Connected");
		
	}

}