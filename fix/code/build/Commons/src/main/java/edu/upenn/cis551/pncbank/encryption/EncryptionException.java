package edu.upenn.cis551.pncbank.encryption;

/**
 * Exception indicating a failure to encrypt or decrypt.
 * 
 * @author jlautman
 *
 */
public class EncryptionException extends Exception {
  // Boilerplate
  private static final long serialVersionUID = 3002068974738017512L;

  public EncryptionException() {
    this(null, null);
  }

  public EncryptionException(String m) {
    this(m, null);
  }

  public EncryptionException(Throwable t) {
    this(null, t);
  }

  public EncryptionException(String m, Throwable t) {
    super(m, t);
  }

}
