package edu.upenn.cis551.pncbank.messaging;

import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class BogusMessager {

  void sendMessage(String message, Socket s, PublicKey destinationPublicKey, PrivateKey senderPrivateKey) throws Exception{ 
    
  }
  
  String receiveMessage(Socket s, PublicKey destinationPublicKey, PrivateKey senderPrivateKey) throws Exception{
    return "Received message";  
  }

}
