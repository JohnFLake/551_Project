package edu.upenn.cis551.pncbank.encryption;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AESEncryption implements IEncryption<SecretKey,SecretKey> {

  
  /**
   * AES Encryption
   */
  @Override
  public String encrypt(String plaintext, SecretKey key) throws EncryptionException {
    try {
      Cipher c = Cipher.getInstance("AES"); 
      c.init(Cipher.ENCRYPT_MODE,key);
      byte[] enc = c.doFinal(plaintext.getBytes()); 
      return Base64.getEncoder().encodeToString(enc);
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null); 
    }
  }

  
  /**
   * AES Decryption
   */
  @Override
  public String decrypt(String ciphertext, SecretKey key) throws EncryptionException {
    try {
      Cipher c = Cipher.getInstance("AES"); 
      c.init(Cipher.DECRYPT_MODE,key);
      byte[] cipherBytes = Base64.getDecoder().decode(ciphertext); 
      String pt = new String(c.doFinal(cipherBytes)); 
      return pt; 
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null);  
    }
  }

  
  /**
   * Generate an AES Key
   */
  @Override
  public SecretKey generateKey() throws EncryptionException {
    try {
      KeyGenerator keygen = KeyGenerator.getInstance("AES"); 
      keygen.init(128); 
      SecretKey sKey = keygen.generateKey(); 
      return sKey;
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null); 
    }
  }
  

}
