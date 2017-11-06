package edu.upenn.cis551.pncbank.encryption;

import static org.junit.Assert.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;

public class EncryptionTest {

  @Test
  public void testEncryptPublicAndDecryptPrivate() {
    Encryption enc = new Encryption(); 
    try {
      KeyPair k = enc.generateRSAKeys(); 
      String pt = "Secret Message"; 
      String ct = enc.encryptRSA(pt, k.getPublic());
      String dec = enc.decryptRSA(ct, k.getPrivate()); 
      assertEquals(pt,dec); 
    }catch(Exception e) {
      fail(e.getMessage()); 
    }
  }
  
  
  
  @Test
  public void testEncryptDecryptAES() {
    Encryption enc = new Encryption(); 
    try {
      SecretKey key = enc.generateAESKey(); 
      String pt = "Secret Message"; 
      String ct = enc.encryptAES(pt, key); 
      String dec = enc.decryptAES(ct, key); 
      assertEquals(pt,dec); 
    }catch(Exception e) {
      e.printStackTrace();
      fail("Failure: " + e.getMessage()); 
    }
  }
  
  
  @Test
  public void testSignAndVerify() {
    Encryption enc = new Encryption(); 
    try {
      KeyPair kPair = enc.generateRSAKeys(); 
      String msg = "Secret"; 
      String signed = enc.sign(msg, kPair.getPrivate());
      assertEquals(true,enc.verify(msg, signed, kPair.getPublic()));
    }catch(Exception e) {
      e.printStackTrace();
      fail("Failure: " + e.getMessage()); 
    }
  }
  
  @Test
  public void testEncryptMessage() {
    Encryption enc = new Encryption(); 
    String message = "This is a message to the bank"; 
    
    try {
      KeyPair kPair = enc.generateRSAKeys(); 
      SecretKey aesKey = enc.generateAESKey(); 
      String signature = enc.sign(message,kPair.getPrivate()); 
      String encrypted = enc.encryptAES(message + "===SIG===" +  signature, aesKey); 
      String encodedKey = Base64.getEncoder().encodeToString(aesKey.getEncoded()); 
      String encKey = enc.encryptRSA(encodedKey, kPair.getPublic());
      
      //We send the encrypted message 
      String toSend = encrypted + "===AES===" + encKey; 
      
      //Now, unpack it. 
      String[] msg = toSend.split("===AES==="); 
      String decAesKey = enc.decryptRSA(msg[1], kPair.getPrivate());
      byte[] decAesBytes = Base64.getDecoder().decode(decAesKey);
      SecretKey decAes = new SecretKeySpec(decAesBytes, 0, decAesBytes.length, "AES");
      String decMessage = enc.decryptAES(msg[0], decAes); 
      
      String[] msg2 = decMessage.split("===SIG==="); 
      assertEquals(message,msg2[0]); 
      assertEquals(true,enc.verify(msg2[0],msg2[1],kPair.getPublic())); 
    }catch(Exception e) {
      e.printStackTrace();
      fail("Failure: " + e.getMessage()); 
    }
  }
  

}
