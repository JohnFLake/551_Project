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

import org.apache.commons.cli.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.DepositPOJO;
import edu.upenn.cis551.pncbank.transaction.WithdrawPOJO; 

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
		options.addOption("n", true, "Create a new account"); 
		options.addOption("d", true, "Deposit money"); 
		options.addOption("w", true, "Withdrawl Money"); 
		options.addOption("g", true, "Check Balance"); 
		CommandLineParser parser = new DefaultParser();

		int port = 3000; 
		String IP = "127.0.0.1";
		String authFile = "bank.auth"; 
		String accountName = "b";
		String cardFile = "b";
		String sendstring = null;

		Atm client = new Atm(authFile, IP, port, cardFile, accountName);
		client.CheckBalance();

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

			
			// get sequence number from card file
			long seqNumber = 0;
			Object send;
			ObjectMapper objectMapper = new ObjectMapper();
			if(cmd.hasOption("g"))
			{
				client.CheckBalance();
			}

			else if(cmd.hasOption("n"))
			{
				String moneystring = cmd.getOptionValue("n");
				int money = Integer.parseInt(moneystring);

				send = new CreateAccountPOJO(accountName,money,seqNumber);
				sendstring = objectMapper.writeValueAsString(send);
			}

			else if(cmd.hasOption("w"))
			{
				String moneystring = cmd.getOptionValue("w");
				long money = Integer.parseInt(moneystring);

				send = new WithdrawPOJO(accountName,money,seqNumber);
				sendstring = objectMapper.writeValueAsString(send);
			}

			else if(cmd.hasOption("d"))
			{
				String moneystring = cmd.getOptionValue("d");
				long money = Integer.parseInt(moneystring);

				send = new DepositPOJO(accountName,money,seqNumber);
				sendstring = objectMapper.writeValueAsString(send);
			}

			String response = null;
			try {
				Socket AtmBank = new Socket(IP,port);
				try {
					response = writeToAndReadFromSocket(AtmBank, sendstring);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			if(cmd.hasOption("g"))
			{
				//client.CheckBalance();
			}

			else if(cmd.hasOption("n"))
			{
				CreateAccountPOJO cResponse = objectMapper.readValue(response, CreateAccountPOJO.class);
				
				// This needs to be fixed because it has to be a JSON string output
				System.out.println("account:" + cResponse.getAccountName() +", initial balance: " + cResponse.getBalance());
				
			}

			else if(cmd.hasOption("w"))
			{
				WithdrawPOJO cWithdraw = objectMapper.readValue(response, WithdrawPOJO.class);
				System.out.println("account:" + cWithdraw.getAccountName() +", withdraw: " + cWithdraw.getWithdraw());
			}

			else if(cmd.hasOption("d"))
			{
				DepositPOJO cDeposit = objectMapper.readValue(response, DepositPOJO.class);
				System.out.println("account:" + cDeposit.getAccountName() +", deposit: " + cDeposit.getDeposit());
			}


		} catch(Exception e) {
			e.printStackTrace(); 
			System.out.println("wrong args fam.");
		}

	}

	public static String writeToAndReadFromSocket(Socket socket, String writeTo) throws Exception
	{
		try 
		{
			// write text to the socket
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(writeTo);
			bufferedWriter.flush();

			// read text from the socket
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = bufferedReader.readLine()) != null)
			{
				sb.append(str + "\n");
			}

			// close the reader, and return the results as a String
			bufferedReader.close();
			return sb.toString();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}
	}

}