//Package for parsing command line arguments

package edu.upenn.cis551.pncbank;

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
		options.addOption("s", false, "The authorization file"); 
		options.addOption("i", false, "The IP address for this server to run on"); 
		options.addOption("p", false, "The port for this server to run on"); 
		options.addOption("c", false, "The customer's ATM card file"); 
		options.addOption("n", false, "Create a new account"); 
		options.addOption("d", false, "Deposit money"); 
		options.addOption("w", false, "Withdrawl Money"); 
		options.addOption("g", false, "Check Balance"); 
		CommandLineParser parser = new DefaultParser(); 
		
		int port = 3000; 
		String IP = "127.0.0.1";
		String authFile = "bank.auth"; 
		String accountName = "";
		String cardFile = "";
		
		try {
			CommandLine cmd = parser.parse(options, args);
			
			if(!cmd.hasOption("n") || !cmd.hasOption("d") || !cmd.hasOption("w") || !cmd.hasOption("g"))
			{
				System.exit(255);	
			}
			
			// make sure they are only doing one type of request
			if(cmd.hasOption("n") && cmd.hasOption("d"))
			{
				System.exit(255);
			}
			
			else if(cmd.hasOption("n") && cmd.hasOption("w"))
			{
				System.exit(255);
			}
			
			else if(cmd.hasOption("n") && cmd.hasOption("g"))
			{
				System.exit(255);
			}
			
			else if(cmd.hasOption("w") && cmd.hasOption("d"))
			{
				System.exit(255);
			}
			
			else if(cmd.hasOption("w") && cmd.hasOption("g"))
			{
				System.exit(255);
			}
			
			else if(cmd.hasOption("g") && cmd.hasOption("d"))
			{
				System.exit(255);
			}
			// Check for account name
			if(cmd.hasOption("a")) {
				accountName = cmd.getOptionValue("a");
				cardFile = accountName + ".card";
			}
			
			else {
				System.exit(255);
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
			
			
			Atm client = new Atm(authFile, IP, port, cardFile, accountName);
			
			if(cmd.hasOption("g"))
			{
				client.CheckBalance();
			}
		
			else if(cmd.hasOption("n"))
			{
				String moneystring = cmd.getOptionValue("n");
				int money = Integer.parseInt(moneystring);
				client.NewAccount(money);
			}
			
			else if(cmd.hasOption("w"))
			{
				String moneystring = cmd.getOptionValue("w");
				int money = Integer.parseInt(moneystring);
				client.Withdrawl(money);
			}
			
			else if(cmd.hasOption("d"))
			{
				String moneystring = cmd.getOptionValue("d");
				int money = Integer.parseInt(moneystring);
				client.Deposit(money);
			}
			
		} catch(Exception e) {
			e.printStackTrace(); 
			System.out.println("wrong args fam.");
		}
		

		System.out.println("Connected");
		
		
	}

}