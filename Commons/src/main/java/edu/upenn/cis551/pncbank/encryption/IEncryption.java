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
public interface IEncryption<EKey, DKey>{
  
  String encrypt(String plaintext,  EKey key) throws EncryptionException; 
  String decrypt(String ciphertext, DKey key) throws EncryptionException; 
  Object generateKey() throws EncryptionException; 
}
