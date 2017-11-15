package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.transaction.response.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class BalanceRequestTest {

  @Test
  public void test_apply_balance_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long balance = 1000000;
    long sequence = 12345;
    TAcctMgr test = new TAcctMgr();
    Optional<Account> oa = test.createAccount(accountName, validator, sequence, balance);
    test.commitAccount(accountName);
    sequence++;
    BalanceRequest bal = new BalanceRequest(accountName, validator, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(bal);
    oa.ifPresent(a -> a.commit(bal.sequenceNumber));

    // Verify
    // State is changed (only sequence number)
    Assert.assertTrue(test.get(accountName, sequence).isPresent());
    Account a = test.get(accountName, sequence).get();
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
    TAcctMgr test = new TAcctMgr();
    BalanceRequest bal = new BalanceRequest(accountName, validator, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(bal);

    // Verify
    // State is unchanged
    Assert.assertFalse(test.get(accountName, sequence).isPresent());

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
    TAcctMgr test = new TAcctMgr();
    Optional<Account> oa = test.createAccount(accountName, validator, sequence, balance);
    test.commitAccount(accountName);
    sequence++;
    BalanceRequest bal = new BalanceRequest(accountName, validator, reqSequence);
    oa.ifPresent(a -> a.commit(bal.sequenceNumber));

    // Run
    Optional<TransactionResponse> ot = test.apply(bal);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName, sequence).isPresent());
    Account a = test.get(accountName, sequence).get();
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
    TAcctMgr test = new TAcctMgr();
    Optional<Account> oa = test.createAccount(accountName, validator, sequence, balance);
    test.commitAccount(accountName);
    sequence++;
    BalanceRequest bal = new BalanceRequest(accountName, validator2, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(bal);
    oa.ifPresent(a -> a.commit(bal.sequenceNumber));

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName, sequence).isPresent());
    Account a = test.get(accountName, sequence).get();
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
