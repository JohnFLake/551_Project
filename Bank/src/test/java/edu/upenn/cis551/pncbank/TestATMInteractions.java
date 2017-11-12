package edu.upenn.cis551.pncbank;

import java.io.IOException;
import javax.crypto.SecretKey;
import org.junit.Before;
import edu.upenn.cis551.pncbank.Bank;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.encryption.AESEncryption;
import edu.upenn.cis551.pncbank.encryption.EncryptionException;
import edu.upenn.cis551.pncbank.encryption.EncryptionPair;

public class TestATMInteractions {
  private Bank bank;
  private EncryptionPair<SecretKey, SecretKey> keys;
  private final AESEncryption aes = new AESEncryption();
  private AccountManager am;
  private int port = 3000;

  @Before
  public void setUp() throws EncryptionException, IOException {
    keys = aes.generateKey();
    am = new AccountManager();
    bank = new Bank(port, keys.getEncryptionKey(), am); // Doesn't work since it isn't an object.
  }
  // TODO actual tests
}
