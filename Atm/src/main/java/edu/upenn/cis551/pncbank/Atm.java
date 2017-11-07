package edu.upenn.cis551.pncbank;

import org.apache.commons.cli.*; 

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
			}
			
			public void NewAccount(int balance)
			{
				// add to the file the account name and intitial balance
			}
			
			public void Deposit(int deposit)
			{
				// go to the file check the name and add money
			}
			
			public void Withdrawl(int withdrawl)
			{
				// go to the file check the name and subtract money
			}
		}	
  

