package edu.upenn.cis551.pncbank.encryption;

/**
 * Pair of the encryption and decryption keys.
 * 
 * @author jlautman
 *
 * @param <D> The type of the decryption key.
 * @param <E> The type of the encryption key.
 */
public class EncryptionKeys<D, E> {
  private final D d;
  private final E e;

  public EncryptionKeys(D d, E e) {
    this.d = d;
    this.e = e;
  }

  public E getE() {
    return this.e;
  }

  public D getD() {
    return this.d;
  }
}
