package bank_server;

//Package for parsing command line arguments
import org.apache.commons.cli.*;

import utility.InputChecker; 

public class Main {
	
	public static void main(String[] args) {
		//Add options: 
		Options options = new Options(); 
		options.addOption("p", true, "The port for this server to run on"); 
		options.addOption("s", true, "The authorization file"); 
		CommandLineParser parser = new DefaultParser(); 
		
		int portNumber = 3000; 
		String authFile = "bank.auth"; 
		
		try {
			CommandLine cmd = parser.parse(options, args);
			
			//Check for port option
			if(cmd.hasOption("p")) {
				String portString = cmd.getOptionValue("p"); 
				if(InputChecker.isValidPortNumber(portString)) {
					portNumber = Integer.parseInt(portString); 
				}else {
					System.exit(255);
				}
			}
			
			//Check for auth file
			if(cmd.hasOption("s")) {
				authFile = cmd.getOptionValue("s"); 
				if(!InputChecker.isValidAuthFile(authFile)) {
					System.exit(255);
				}
			} 
			
		} catch(Exception e) {
			e.printStackTrace(); 
			System.exit(255); 
		}
		
		Server bankServer = new Server(portNumber,authFile); 
		bankServer.makeAuthFile(); 
	}

}
