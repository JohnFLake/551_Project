package edu.upenn.cis551.pncbank;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.crypto.SecretKey;

public class atmSession {
	private static String IP;
	private static int port;
	private static SecretKey auth;
	
	public atmSession(String IP, int port, SecretKey auth) {
		this.IP = IP;
		this.port = port;
		this.auth = auth;
	}
	
	public String getIP()
	{
		return IP;
	}
	
	public  int getPort()
	{
		return port;
	}
	
	public  SecretKey getsecretkey()
	{
		return auth;
	}
	
	
	public static String writeToAndReadFromSocket(Socket socket, String writeTo) throws Exception
	{
		try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) 
		{
			// write text to the socket
			
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