package edu.upenn.cis551.pncbank.encryption;

import org.junit.Assert;
import org.junit.Test;

public class NoEncryptionTest {

  @Test
  public void test_encryptionInvariant() throws EncryptionException {
    NoEncryption test = new NoEncryption();
    EncryptionKeys<Object, Object> keys = test.genKeys();
    String value = "Testtesttest123";
    Assert.assertEquals(//
        value, //
        test.decrypt(//
            test.encrypt(value, keys.getE()), keys.getD()));
  }

}
