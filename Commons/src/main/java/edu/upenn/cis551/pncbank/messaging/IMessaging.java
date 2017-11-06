package edu.upenn.cis551.pncbank.messaging;

import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface IMessaging {
  
  /**
   * Send properly encrypted bytes to the given socket. 
   * @param bytesToSend Bytes (pre-encryption) we will send
   */
  void sendMessage(String message, Socket s, PublicKey destinationPublicKey, PrivateKey senderPrivateKey) throws Exception; 
  
  /**
   * Get a valid, decrypted message from the given socket.  Blocks until a message comes. 
   * @param s Socket we are listening to. 
   * @return Decrypted bytes we've received. 
   */
  String receiveMessage(Socket s, PublicKey destinationPublicKey, PrivateKey senderPrivateKey) throws Exception; 

}
