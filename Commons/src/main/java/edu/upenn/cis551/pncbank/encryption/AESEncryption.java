package edu.upenn.cis551.pncbank.encryption;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AESEncryption implements IEncryption<SecretKey, SecretKey> {


  /**
   * AES Encryption
   */
  @Override
  public byte[] encrypt(byte[] plaintext, SecretKey key) throws EncryptionException {
    try {
      Cipher c = Cipher.getInstance("AES");
      c.init(Cipher.ENCRYPT_MODE, key);
      return c.doFinal(plaintext);
    } catch (Exception e) {
      throw new EncryptionException(e.getMessage(), null);
    }
  }


  /**
   * AES Decryption
   */
  @Override
  public byte[] decrypt(byte[] ciphertext, SecretKey key) throws EncryptionException {
    try {
      Cipher c = Cipher.getInstance("AES");
      c.init(Cipher.DECRYPT_MODE, key);
      return c.doFinal(ciphertext);
    } catch (Exception e) {
      throw new EncryptionException(e.getMessage(), null);
    }
  }


  /**
   * Generate an AES Key
   */
  @Override
  public EncryptionPair<SecretKey, SecretKey> generateKey() throws EncryptionException {
    try {
      KeyGenerator keygen = KeyGenerator.getInstance("AES");
      keygen.init(128);
      SecretKey sKey = keygen.generateKey();
      return new EncryptionPair<SecretKey, SecretKey> (sKey, sKey);
    } catch (Exception e) {
      throw new EncryptionException(e.getMessage(), null);
    }
  }


}
