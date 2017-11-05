package edu.upenn.cis551.pncbank.encryption;

/**
 * Interface for an encryption method. Satisfies the constraint that <code>s == decrypt(encrypt(s,
 * encryptKey), decryptKey)</code>
 * 
 * @author jlautman
 *
 */
public interface IEncryption<E, D> {
  /**
   * Decrypts data using the provided key.
   * 
   * @param encrypted The encrypted data.
   * @param key They key used to encrypt the data.
   * @return The decrypted data.
   * @throws EncryptionException On failure to decrypt.
   */
  String decrypt(String encrypted, D key) throws EncryptionException;

  /**
   * Decrypts a string using the provided key.
   * 
   * @param encrypted The encrypted data.
   * @param key They key used to encrypt the data.
   * @return The encrypted data.
   * @throws EncryptionException On failure to encrypt.
   */
  String encrypt(String raw, E key) throws EncryptionException;
}
