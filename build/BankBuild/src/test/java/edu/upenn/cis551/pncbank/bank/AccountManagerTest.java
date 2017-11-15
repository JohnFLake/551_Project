package edu.upenn.cis551.pncbank.bank;

import java.util.Optional;
import org.junit.Test;
import org.testng.Assert;

public class AccountManagerTest {
  @Test
  public void test_createAccount_ok() {
    String accountName = "test";
    String accountName2 = "test2";
    String validator = "validator";
    String validator2 = "validator2";
    long sequence = 12345;
    long sequence2 = 12345;
    long amount = 100000;
    long amount2 = 100000;
    AccountManager test = new AccountManager();
    Optional<Account> oa1 = test.createAccount(accountName, validator, sequence, amount);
    Optional<Account> oa2 = test.createAccount(accountName2, validator2, sequence2, amount2);

    Assert.assertTrue(oa1.isPresent());
    Assert.assertTrue(oa2.isPresent());

    Account a1 = oa1.get();
    Assert.assertEquals(a1.getCardValidator(), validator);
    Assert.assertEquals(a1.getSequence(), sequence + 1);
    Assert.assertEquals(a1.getBalance().longValue(), amount);
    Assert.assertNull(a1.pending);

    Account a2 = oa2.get();
    Assert.assertEquals(a2.getCardValidator(), validator2);
    Assert.assertEquals(a2.getSequence(), sequence2 + 1);
    Assert.assertEquals(a2.getBalance().longValue(), amount2);
    Assert.assertNull(a2.pending);

  }

  @Test
  public void test_createAccount_duplicate() {
    String accountName = "test";
    String validator = "validator";
    long sequence = 12345;
    long amount = 100000;
    AccountManager test = new AccountManager();
    Optional<Account> oa1 = test.createAccount(accountName, validator, sequence, amount);
    Optional<Account> oa2 = test.createAccount(accountName, validator, sequence, amount);

    Assert.assertTrue(oa1.isPresent());
    Assert.assertFalse(oa2.isPresent());

    Account a1 = oa1.get();
    Assert.assertEquals(a1.getCardValidator(), validator);
    Assert.assertEquals(a1.getSequence(), sequence + 1);
    Assert.assertEquals(a1.getBalance().longValue(), amount);
    Assert.assertNull(a1.pending);

  }

}
