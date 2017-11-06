package edu.upenn.cis551.pncbank.encryption;

import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Interface for an encryption method. Satisfies the constraint that <code>s == decrypt(encrypt(s,
 * encryptKey), decryptKey)</code>
 * 
 * @author jlautman
 *
 */
public interface IEncryption{
  
  /*
   * Methods to encrypt and decrypt RSA, as well as to make a keypair. 
   */
  String encryptRSA(String plaintext, PublicKey key) throws EncryptionException; 
  String decryptRSA(String ciphertext, PrivateKey key) throws EncryptionException;
  KeyPair generateRSAKeys() throws EncryptionException;
  
  /*
   * Methods to encrypt and decrypt AES, as well as to make an AES key. 
   */
  String encryptAES(String plaintext, SecretKey aesKey) throws EncryptionException;
  String decryptAES(String ciphertext,SecretKey aesKey) throws EncryptionException;
  SecretKey generateAESKey() throws EncryptionException;
  
  
  /*
   * Methods to sign and verify messages with RSA
   */
  String sign(String plaintext, PrivateKey key) throws EncryptionException;
  boolean verify(String plaintext, String signature, PublicKey key) throws EncryptionException;
 
}
