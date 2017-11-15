package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class CreateAccountRequestTest {

  AccountManager am;

  @Before
  public void setup() {
    am = new AccountManager();
  }

  @Test
  public void test_apply_create_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long initialBalance = 10000;
    long sequence = 12345;
    CreateAccountRequest create =
        new CreateAccountRequest(accountName, validator, initialBalance, sequence);

    // Run
    Optional<TransactionResponse> ot = am.apply(create);
    am.commitAccount(accountName);

    // Verify
    // State is now correct
    Optional<Account> oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
    Assert.assertEquals(sequence + 1, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(initialBalance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertTrue(t.isOk());
  }

  @Test
  public void test_apply_create_duplicate() {
    // Setup
    String accountName = "test";
    String validator1 = "validator1";
    String validator2 = "validator2";
    long initialBalance1 = 10000;
    long initialBalance2 = 20000;
    long sequence1 = 12345;
    long sequence2 = 64234;

    am.createAccount(accountName, validator1, sequence1, initialBalance1);
    am.commitAccount(accountName);
    sequence1++;
    CreateAccountRequest create =
        new CreateAccountRequest(accountName, validator2, initialBalance2, sequence2);

    // Run
    Optional<TransactionResponse> ot = am.apply(create);

    // Verify
    Optional<Account> oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
    Assert.assertEquals(validator1, a.getCardValidator());
    Assert.assertEquals(sequence1, a.getSequence());
    Assert.assertEquals(initialBalance1, a.getBalance().longValueExact());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence2, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_create_duplicate_butuncommitted() {
    // Setup
    String accountName = "test";
    String validator1 = "validator1";
    String validator2 = "validator2";
    long initialBalance1 = 10000;
    long initialBalance2 = 20000;
    long sequence1 = 12345;
    long sequence2 = 64234;

    am.createAccount(accountName, validator1, sequence1, initialBalance1);
    CreateAccountRequest create =
        new CreateAccountRequest(accountName, validator2, initialBalance2, sequence2);

    // Run
    Optional<TransactionResponse> ot = am.apply(create);
    am.commitAccount(accountName);

    // Verify
    Optional<Account> oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
    Assert.assertEquals(validator2, a.getCardValidator());
    Assert.assertEquals(sequence2 + 1, a.getSequence()); // Account now expecting next number
    Assert.assertEquals(initialBalance2, a.getBalance().longValueExact());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence2, t.getSequence()); // Respond with the request sequence number
    Assert.assertTrue(t.isOk());
  }

  @Test
  public void test_apply_create_insufficientbalance() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long initialBalance = 10;
    long sequence = 12345;
    AccountManager test = new AccountManager();
    CreateAccountRequest create =
        new CreateAccountRequest(accountName, validator, initialBalance, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(create);

    // Verify
    // Account was not made
    Assert.assertFalse(am.isPending(accountName));
    Optional<Account> oa = am.get(accountName);
    Assert.assertFalse(oa.isPresent());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());

  }

}
