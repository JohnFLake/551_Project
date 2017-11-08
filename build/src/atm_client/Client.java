package atm_client;

import org.apache.commons.cli.*; 
import java.io.*;
import java.net.*;
import java.security.Key;
import java.util.*;

public class Client {

	private String accountName; 
	private String authFile;
	private String IP;
	private String cardFile;
	private int port;
	
	public Client(String s, String i, int p, String c, String a) {
		port = p; 
		authFile = s; 
		accountName = a;
		cardFile = c;
		IP = i;
	}
	
	public void CheckBalance()
	{
		
	}
	
	public void NewAccount()
	{
		
	}
	
	public void Deposit()
	{
		
	}
	
	public void Withdrawl()
	{
		
	}
}
