package edu.upenn.cis551.pncbank;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class Atm {

			private String accountName; 
			private String authFile;
			private String IP;
			private String cardFile;
			private int port;
			
			public Atm(String s, String i, int p, String c, String a) {
				this.port = p; 
				this.authFile = s; 
				this.accountName = a;
				this.cardFile = c;
				this.IP = i;
			}
			
			public void CheckBalance()
			{
				// go into the file, check the account name and print the money they have
				File checkcard = new File(accountName);
				FileReader reader = null;
				String amount = null;
				if(!checkcard.exists())
				{
					System.exit(255);
				}
				
			    try {
			        reader = new FileReader(checkcard);
			        char[] chars = new char[(int) checkcard.length()];
			        reader.read(chars);
			        amount = new String(chars);
			        reader.close();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			    
			    System.out.println(amount);
			}
			
			public void NewAccount(int balance)
			{
				// add to the file the account name and initial balance
				
				File newCard = new File(accountName);
				
				// Check if account exists and if so return error
				if (newCard.exists())
				{
					System.exit(255);
				}
				
				try {
					BufferedWriter output = new BufferedWriter(new FileWriter(newCard));
					output.write(Integer.toString(balance));
					output.close(); 
				}
				catch(Exception e) {
					e.printStackTrace();	
				}
				
			}
			
			public void Deposit(int deposit)
			{
				// go to the file check the name and add money
				
				File checkcard = new File(accountName);
				FileReader reader = null;
				String amount = null;
				if(!checkcard.exists())
				{
					System.exit(255);
				}
				
			    try {
			        reader = new FileReader(checkcard);
			        char[] chars = new char[(int) checkcard.length()];
			        reader.read(chars);
			        amount = new String(chars);
			        reader.close();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			    
			    int oldAmount = Integer.parseInt(amount);
			    int newAmount = oldAmount + deposit;
			    String newDeposit = Integer.toString(newAmount);
			    
			  
			    try {
				String line = "";
				String oldtext = "";
			    	reader = new FileReader(checkcard);
			    	BufferedReader br = new BufferedReader(reader);
				//BufferedWriter output = new BufferedWriter(new FileWriter(checkcard));
			    while((line = br.readLine()) != null)
			    {
			    		oldtext += line;
			    }
			    	br.close();
			    	String newtext = oldtext.replaceAll(amount, newDeposit);

			    	FileWriter writer = new FileWriter(checkcard);

			    	writer.write(newtext);
			    	writer.close();
			    	reader.close();
			    	
			    }   catch (IOException e) {
			        e.printStackTrace();
			    }
			    
			    
			}
			
			public void Withdrawl(int withdrawl)
			{
				// go to the file check the name and subtract money
				
				File checkcard = new File(accountName);
				FileReader reader = null;
				String amount = null;
				if(!checkcard.exists())
				{
					System.exit(255);
				}
				
			    try {
			        reader = new FileReader(checkcard);
			        char[] chars = new char[(int) checkcard.length()];
			        reader.read(chars);
			        amount = new String(chars);
			        reader.close();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			    
			    int oldAmount = Integer.parseInt(amount);
			    int newAmount = oldAmount - withdrawl;
			    String newDeposit = Integer.toString(newAmount);
			    
			  
			    try {
				String line = "";
				String oldtext = "";
			    	reader = new FileReader(checkcard);
			    	BufferedReader br = new BufferedReader(reader);
				//BufferedWriter output = new BufferedWriter(new FileWriter(checkcard));
			    while((line = br.readLine()) != null)
			    {
			    		oldtext += line;
			    }
			    	br.close();
			    	String newtext = oldtext.replaceAll(amount, newDeposit);

			    	FileWriter writer = new FileWriter(checkcard);

			    	writer.write(newtext);
			    	writer.close();
			    	reader.close();
			    	
			    }   catch (IOException e) {
			        e.printStackTrace();
			    }
			}
		}	
  

