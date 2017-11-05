package edu.upenn.cis551.pncbank.encryption;

/**
 * Interface for an encryption method. Satisfies the constraint that <code>s == decrypt(encrypt(s,
 * encryptKey), decryptKey)</code>
 * 
 * @author jlautman
 *
 */
public interface IEncryption<D, E> {
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

  /**
   * Generate a new pair of decryption and encryption keys for this encryption schema.
   * 
   * @return An EncryptionKeys containing a new set of keys. May not be null, but they contained
   *         keys may be if the schema doesn't need them.
   */
  EncryptionKeys<D, E> genKeys();
}
