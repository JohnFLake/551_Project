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
  public void testAESEncryptDecrypt() throws Exception{
    AESEncryption enc = new AESEncryption(); 
    SecretKey k = enc.generateKey(); 
    String message = "Secret Message"; 
    String ct = enc.encrypt(message, k); 
    String pt = enc.decrypt(ct, k); 
    assertEquals(message,pt); 
  }
  
  @Test
  public void makeAuthFileAndGetKey() throws Exception {
    SecretKey k = Authentication.generateAuthFile("bank.auth"); 
  
    String kString = Base64.getEncoder().encodeToString(k.getEncoded()); 
    SecretKey other = Authentication.getAESKeyFromAuthFile("bank.auth"); 
    String otherString = Base64.getEncoder().encodeToString(other.getEncoded()); 
    assertEquals(kString,otherString); 
  }
  
  @Test
  public void testSaveCardFile() throws Exception{
    CardFile f = new CardFile("myaccount"); 
    Authentication.saveCardFile("myCardFile", f);
    CardFile c = Authentication.getCardFile("myCardFile"); 
    System.out.println(c.getAccountName());
    System.out.println(c.getPin()); 
    System.out.println(c.getSequenceNumber()); 
  }
  

}