package edu.upenn.cis551.pncbank.messaging;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.SecretKey;
import edu.upenn.cis551.pncbank.encryption.Encryption;

import org.json.*; 

public class Messager {
  
  private Encryption enc; 
  
  public Messager() {
    enc = new Encryption();
  }

  
  void sendMessage(String message, Socket s, PublicKey destinationPublicKey, PrivateKey senderPrivateKey) throws Exception {
    try {
      OutputStream os = s.getOutputStream(); 
      
      
      /*
       * Generate AES key. 
       */
      SecretKey aesKey = enc.generateAESKey();
      
      /*
       * Encrypt this AES key with the destination's public key. 
       */
      byte[] keyBytes = aesKey.getEncoded(); 
      String kString = Base64.getEncoder().encodeToString(keyBytes); 
      String encryptedKey = enc.encryptRSA(kString, destinationPublicKey); 
     
      
      /*
       * Sign the message
       */
      String signature = enc.sign(message, senderPrivateKey); 
      JSONObject obj = new JSONObject(); 
      obj.put("message", message); 
      obj.put("signature", signature);   
      String jsonString = obj.toString(); 
      
      /*
       * Encrypt the message
       */
      String ciphertext = enc.encryptAES(jsonString, aesKey); 
      
      //Make a new JSON object for the encrypted message and encrypted AES key: 
      JSONObject messageObject = new JSONObject(); 
      messageObject.put("ciphertext", ciphertext); 
      messageObject.put("encryptedKey", encryptedKey); 
      String stringToSend = messageObject.toString(); 
      System.out.println(stringToSend);
      
      os.write(stringToSend.getBytes()); 
    }catch(Exception e) {
      throw e; 
    }  
  }

  String receiveMessage(Socket s, PublicKey destinationPublicKey, PrivateKey senderPrivateKey) throws Exception {
    try {
      InputStream is = s.getInputStream(); 
      byte[] result = new byte[0];
      byte[] buff = new byte[1024];
      int res = -1;
      while((res =  is.read(buff, 0, buff.length)) > -1) {
          byte[] temp = new byte[result.length + res]; 
          System.arraycopy(result, 0, temp, 0, result.length); 
          System.arraycopy(buff, 0, temp, result.length, res); 
          result = temp;  
      }
      String message = new String(result); 
      
      //TODO: unpack everything from the string. 
      return message; 
    }catch(Exception e) {
      throw e; 
    }
  }
}
