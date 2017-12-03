package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.transaction.response.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class BalanceRequestTest {

  AccountManager am;

  @Before
  public void setUp() {
    am = new AccountManager();
  }

  @Test
  public void test_apply_balance_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long balance = 1000000;
    long sequence = 12345;
    Optional<Account> oa = am.createAccount(accountName, validator, sequence, balance);
    am.commitAccount(accountName);
    sequence++;
    BalanceRequest bal = new BalanceRequest(accountName, validator, sequence);

    // Run
    Optional<TransactionResponse> ot = am.apply(bal);
    oa.ifPresent(a -> a.commit(bal));

    // Verify
    // State is changed (only sequence number)
    Assert.assertTrue(am.get(accountName, sequence + 1).isPresent() && oa.isPresent());
    Account a = oa.get();
    Assert.assertEquals(sequence + 1, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(balance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertTrue(t instanceof BalanceResponse);
    BalanceResponse b = (BalanceResponse) t;
    Assert.assertEquals("10000.00", b.getBalance());
    Assert.assertTrue(b.isOk());
    Assert.assertEquals(sequence, b.getSequence());
    Assert.assertEquals(accountName, b.getAccountId());
  }

  @Test
  public void test_apply_balance_uncommittedAccount() {
    // Case when the bank hasn't yet gotten an ack on the account creation. It should notice that
    // the sequence number is one more than the pending transaction, confirm the account, and then
    // proceed as normal.

    // Setup
    String accountName = "test";
    String validator = "validator";
    long balance = 1000000;
    long sequence = 12345;
    am.createAccount(accountName, validator, sequence, balance);
    sequence++;
    // Request has the next sequence number
    BalanceRequest bal = new BalanceRequest(accountName, validator, sequence);

    // Run
    Optional<TransactionResponse> ot = am.apply(bal);
    Assert.assertFalse(am.isPending(accountName));
    am.get(accountName, sequence).ifPresent(a -> a.commit(bal));

    // Verify
    // State is changed (only sequence number)
    Assert.assertFalse(am.isPending(accountName)); // No longer pending
    Assert.assertTrue(am.get(accountName, sequence + 1).isPresent());
    Account a = am.get(accountName, sequence + 1).get();
    Assert.assertEquals(sequence + 1, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(balance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertTrue(t instanceof BalanceResponse);
    BalanceResponse b = (BalanceResponse) t;
    Assert.assertEquals("10000.00", b.getBalance());
    Assert.assertTrue(b.isOk());
    Assert.assertEquals(sequence, b.getSequence());
    Assert.assertEquals(accountName, b.getAccountId());
  }

  @Test
  public void test_apply_balance_noaccount() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long sequence = 12345;
    BalanceRequest bal = new BalanceRequest(accountName, validator, sequence);

    // Run
    Optional<TransactionResponse> ot = am.apply(bal);

    // Verify
    // State is unchanged
    Assert.assertFalse(am.get(accountName, sequence).isPresent());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertFalse(t instanceof BalanceResponse);
    Assert.assertFalse(t.isOk());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertEquals(accountName, t.getAccountId());
  }

  @Test
  public void test_apply_balance_wrongsequence() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long balance = 1000000;
    long sequence = 12345;
    long reqSequence = 63457234;
    Optional<Account> oa = am.createAccount(accountName, validator, sequence, balance);
    am.commitAccount(accountName);
    sequence++;
    BalanceRequest bal = new BalanceRequest(accountName, validator, reqSequence);
    oa.ifPresent(a -> a.commit(bal));

    // Run
    Optional<TransactionResponse> ot = am.apply(bal);

    // Verify
    // State is unchanged
    Assert.assertTrue(am.get(accountName, sequence).isPresent());
    Account a = am.get(accountName, sequence).get();
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(balance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertFalse(t instanceof BalanceResponse);
    Assert.assertFalse(t.isOk());
    Assert.assertEquals(reqSequence, t.getSequence()); // Tells the atm the correct sequence number
    Assert.assertEquals(accountName, t.getAccountId());
  }

  @Test
  public void test_apply_balance_wrongvalidator() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    String validator2 = "validator2";
    long balance = 1000000;
    long sequence = 12345;
    Optional<Account> oa = am.createAccount(accountName, validator, sequence, balance);
    am.commitAccount(accountName);
    sequence++;
    BalanceRequest bal = new BalanceRequest(accountName, validator2, sequence);

    // Run
    Optional<TransactionResponse> ot = am.apply(bal);
    oa.ifPresent(a -> a.commit(bal));

    // Verify
    // State is unchanged
    Assert.assertTrue(am.get(accountName, sequence).isPresent());
    Account a = am.get(accountName, sequence).get();
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(balance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertFalse(t instanceof BalanceResponse);
    Assert.assertFalse(t.isOk());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertEquals(accountName, t.getAccountId());
  }

}
