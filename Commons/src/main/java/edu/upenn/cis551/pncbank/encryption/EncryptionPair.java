package edu.upenn.cis551.pncbank.encryption;

public class EncryptionPair<EKey, DKey> {
  private final EKey encryptionKey;
  private final DKey decryptionKey;

  EncryptionPair(EKey encryptionKey, DKey decryptionKey) {
    this.encryptionKey = encryptionKey;
    this.decryptionKey = decryptionKey;
  }

  /**
   * @return the encryptionKey
   */
  public final EKey getEncryptionKey() {
    return this.encryptionKey;
  }

  /**
   * @return the decryptionKey
   */
  public final DKey getDecryptionKey() {
    return this.decryptionKey;
  }
}
