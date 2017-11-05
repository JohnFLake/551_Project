package edu.upenn.cis551.pncbank.encryption;

/**
 * Implementation of an encryption that does nothing.
 * 
 * @author jlautman
 *
 */
public class NoEncryption implements IEncryption<Object, Object> {

  public String decrypt(String encrypted, Object key) throws EncryptionException {
    return encrypted;
  }

  public String encrypt(String raw, Object key) throws EncryptionException {
    return raw;
  }

}
