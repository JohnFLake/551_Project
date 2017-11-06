package edu.upenn.cis551.pncbank.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encryption implements IEncryption {

  /**
   * Encrypt using an RSA public key. 
   */
  public String encryptRSA(String plaintext, PublicKey key)throws EncryptionException {   
    try {
      Cipher c = Cipher.getInstance("RSA"); 
      c.init(Cipher.ENCRYPT_MODE, key);
      byte[] cipherBytes = c.doFinal(plaintext.getBytes()); 
      String ct = Base64.getEncoder().encodeToString(cipherBytes); 
      return ct; 
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null); 
    }
  }
  
  /**
   * Decrypt using an RSA private key. 
   */
  public String decryptRSA(String ciphertext, PrivateKey key)throws EncryptionException {
    try {
      Cipher c = Cipher.getInstance("RSA"); 
      c.init(Cipher.DECRYPT_MODE, key);
      byte[] cipherBytes = Base64.getDecoder().decode(ciphertext); 
      String pt = new String(c.doFinal(cipherBytes));  
      return pt; 
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null); 
    }  
  }
  
  /**
   * Generate a 2048 bit RSA public/private keypair. 
   */
  public KeyPair generateRSAKeys() throws EncryptionException{
    try {
      final int keySize = 2048;
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(keySize);      
      KeyPair k = keyPairGenerator.generateKeyPair(); 
      return k; 
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null); 
    }
  }
  
  /**
   * Encrypt using AES
   */
  public String encryptAES(String plaintext, SecretKey aesKey) throws EncryptionException{
    try {
      Cipher c = Cipher.getInstance("AES"); 
      c.init(Cipher.ENCRYPT_MODE,aesKey);
      byte[] enc = c.doFinal(plaintext.getBytes()); 
      return Base64.getEncoder().encodeToString(enc);
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null); 
    }
    
  }
  
  /**
   * Decrypt using AES
   */
  public String decryptAES(String ciphertext, SecretKey aesKey) throws EncryptionException {
    try {
      Cipher c = Cipher.getInstance("AES"); 
      c.init(Cipher.DECRYPT_MODE,aesKey);
      byte[] cipherBytes = Base64.getDecoder().decode(ciphertext); 
      String pt = new String(c.doFinal(cipherBytes)); 
      return pt; 
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null);  
    }
  }
  
  /**
   * Make a new AES key. 
   */
  public SecretKey generateAESKey() throws EncryptionException{
    try {
      KeyGenerator keygen = KeyGenerator.getInstance("AES") ; // key generator to be used with AES algorithm.
      keygen.init(128) ; // Key size is specified here.
      SecretKey sKey = keygen.generateKey(); 
      return sKey;
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null); 
    }
  }
  
  
  /**
   * Sign the given message with a private key
   */
  public String sign(String plaintext, PrivateKey key) throws EncryptionException{ 
    try {
      Signature sign = Signature.getInstance("SHA256withRSA");
      sign.initSign(key); 
      sign.update(plaintext.getBytes());
      byte[] signed = sign.sign(); 
      return Base64.getEncoder().encodeToString(signed); 
    }catch(Exception e) {
      throw new EncryptionException(e.getMessage(),null);  
    }
  }
  
  
  
  /**
   * Verify that a message was signed by a particular person
   */
  public boolean verify(String plaintext, String signature, PublicKey key)throws EncryptionException{
    try {
      Signature sign = Signature.getInstance("SHA256withRSA");
      sign.initVerify(key); 
      sign.update(plaintext.getBytes());
      byte[] signed = Base64.getDecoder().decode(signature); 
      return sign.verify(signed); 
    }catch(Exception e) {
      e.printStackTrace();
      throw new EncryptionException(e.getMessage(),null); 
    }

  }
}
