package edu.upenn.cis551.pncbank.encryption;

/**
 * Interface for an encryption method. Satisfies the constraint that <code>s == decrypt(encrypt(s,
 * encryptKey), decryptKey)</code>
 * 
 * @author jlautman
 *
 */
public interface IEncryption<EKey, DKey>{
  
  byte[] encrypt(byte[] plaintext,  EKey key) throws EncryptionException; 
  byte[] decrypt(byte[] ciphertext, DKey key) throws EncryptionException; 
  EncryptionPair<EKey, DKey> generateKey() throws EncryptionException; 
}
