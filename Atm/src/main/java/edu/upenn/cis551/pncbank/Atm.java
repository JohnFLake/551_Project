package edu.upenn.cis551.pncbank;

import org.apache.commons.cli.*;

import edu.upenn.cis551.pncbank.encryption.Authentication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import javax.crypto.SecretKey;

public class Atm {

	private static String accountName; 
	private static String authFile;
	private static String IP;
	private static String cardFile;
	private static int port;

	public Atm(String s, String i, int p, String c, String a) {
		this.port = p; 
		this.authFile = s; 
		this.accountName = a;
		this.cardFile = c;
		this.IP = i;
	}



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


	public static boolean properTransaction(CommandLine cmd)
	{
		if(!cmd.hasOption("n") || !cmd.hasOption("d") || !cmd.hasOption("w") || !cmd.hasOption("g"))
		{
			return false;	
		}

		// make sure they are only doing one type of request
		if(cmd.hasOption("n") && cmd.hasOption("d"))
		{
			return false;	
		}

		else if(cmd.hasOption("n") && cmd.hasOption("w"))
		{
			return false;	
		}

		else if(cmd.hasOption("n") && cmd.hasOption("g"))
		{
			return false;	
		}

		else if(cmd.hasOption("w") && cmd.hasOption("d"))
		{
			return false;	
		}

		else if(cmd.hasOption("w") && cmd.hasOption("g"))
		{
			return false;	
		}

		else if(cmd.hasOption("g") && cmd.hasOption("d"))
		{
			return false;	
		}

		return true;
	}


	public static String cardFile(CommandLine cmd)
	{
		if(cmd.hasOption("a")) {
			String accountName = cmd.getOptionValue("a");
			String cardFile = accountName + ".card";
		}

		else {
			System.exit(255);
		}

		return cardFile;
	}


	public static SecretKey authKey(String authFile) {
		SecretKey authKey = null;

		if(!(new File(authFile+".auth")).exists())
		{
			try {
				authKey = Authentication.generateAuthFile(authFile+".auth");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			try {
				authKey = Authentication.getAESKeyFromAuthFile(authFile + ".auth");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return authKey;
	}
}	
  

