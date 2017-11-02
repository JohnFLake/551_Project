package bank_server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.security.Key;

import utility.Crypto;

public class Server {
	
	private int port; 
	private String authFileName; 
	private Key aesKey; 
	
	public Server(int p, String s) {
		port = p; 
		authFileName = s; 
	}
	
	public void makeAuthFile() {
		//Just generate an AES key and store it in a file. 
		aesKey = Crypto.generateKey(); 
		String encKey = Crypto.exportKeyAsBase64(aesKey); 
		File auth = new File(authFileName); 
		
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(auth));
			output.write(encKey);
			output.close(); 
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		
		System.out.println("created");
	}
	

}
