//Package for parsing command line arguments

package edu.upenn.cis551.pncbank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.crypto.SecretKey;

import org.apache.commons.cli.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.upenn.cis551.pncbank.encryption.Authentication;
import edu.upenn.cis551.pncbank.encryption.CardFile;
import edu.upenn.cis551.pncbank.transaction.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.DepositPOJO;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;
import edu.upenn.cis551.pncbank.transaction.WithdrawPOJO; 

public class Main {
	
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
		String sendstring = null;
		SecretKey authKey = null;

		try {
			CommandLine cmd = parser.parse(options, args);

			// Checks for proper ATM input
			if(!Atm.properTransaction(cmd))
			{
				System.exit(255);
			}
			
			Atm.lengthChecker(cmd.getOptionValue("a"));
			if(!cmd.hasOption("a")) {
				System.exit(255);
			}
			
			else {
				accountName = cmd.getOptionValue("a");
			}
			
			Atm.lengthChecker(cmd.getOptionValue("c"));
			// Check for account name and update card file name


			// Check for IP address
			if(cmd.hasOption("i")) {
				Atm.lengthChecker(cmd.getOptionValue("i"));
				IP = cmd.getOptionValue("i"); 
			} 
			
			//Check for port option
			if(cmd.hasOption("p")) {
				Atm.lengthChecker(cmd.getOptionValue("p"));
				String portString = cmd.getOptionValue("p"); 
				if(Atm.isValidPortNumber(portString)) {
					port = Integer.parseInt(portString); 
				}else {
					System.out.println("Invalid port number.");
					System.exit(255);
				}
			}

			//Check for auth file
			//TODO: Add check for valid auth file. 
			if(cmd.hasOption("s")) {
				if(cmd.getOptionValue("s") != "bank.auth")
				{
					System.exit(255);
				}
			}
			
			else {
			//	 authKey = Authentication.getAESKeyFromAuthFile("/Users/ryanhub/git/551_Project/Bank/bank.auth");
				}

			atmSession session = new atmSession(IP,port,authKey);
			
			
			// Check for transaction option and execute
			if(cmd.hasOption("n"))
			{
				Atm.lengthChecker(cmd.getOptionValue("n"));
				int amount = 0;
				try { 
				amount = Integer.parseInt(cmd.getOptionValue("n"));
				}
				catch (NumberFormatException ex){
					System.exit(255);
				}
		
				atmclient.newAccount(session, accountName, amount);
			}
			
			else if(cmd.hasOption("d"))
			{
				Atm.lengthChecker(cmd.getOptionValue("d"));
				try { 
				Integer.parseInt(cmd.getOptionValue("n"));
				}
				catch (NumberFormatException ex){
					System.exit(255);
				}
				
				atmclient.Deposit(session, accountName, Integer.parseInt(cmd.getOptionValue("d")));
			}
			
			else if(cmd.hasOption("w"))
			{
				Atm.lengthChecker(cmd.getOptionValue("w"));
				try { 
				Integer.parseInt(cmd.getOptionValue("n"));
				}
				catch (NumberFormatException ex){
					System.exit(255);
				}
				
				atmclient.Withdraw(session, accountName, Integer.parseInt(cmd.getOptionValue("w")));
			}		
			
			else if(cmd.hasOption("g"))
			{
				Atm.lengthChecker(cmd.getOptionValue("g"));
				atmclient.checkBalance(session, accountName);
			}
			
				
		} catch(Exception e) {
			e.printStackTrace(); 
			System.out.println("wrong args fam.");
		}

	}
}