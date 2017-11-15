package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class WithdrawRequestTest {

  @Test
  public void test_toString_0cents() {
    WithdrawRequest test = new WithdrawRequest("testacct", "valid", 100l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"withdraw\":1}", test.toString());
  }

  @Test
  public void test_toString_10cents() {
    WithdrawRequest test = new WithdrawRequest("testacct", "valid", 110l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"withdraw\":1.1}", test.toString());
  }

  @Test
  public void test_toString_11cents() {
    WithdrawRequest test = new WithdrawRequest("testacct", "valid", 111l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"withdraw\":1.11}", test.toString());
  }

  @Test
  public void test_toString_largestNumber() {
    WithdrawRequest test =
        new WithdrawRequest("testacct", "valid", AbstractRequest.maxCurrencyValue, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"withdraw\":4294967295.99}", test.toString());
  }



  @Test
  public void test_apply_withdraw_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = 100;
    TAcctMgr test = new TAcctMgr();
    Optional<Account> oa = test.createAccount(accountName, validator, sequence, oldBalance);
    test.commitAccount(accountName);
    sequence++;
    WithdrawRequest deposit = new WithdrawRequest(accountName, validator, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);
    oa.get().commit(sequence);

    // Verify
    // State is changed
    Assert.assertTrue(test.get(accountName, sequence).isPresent());
    Account a = test.get(accountName, sequence).get();
    Assert.assertEquals(sequence + 1, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(oldBalance - amount), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertTrue(t.isOk());
  }

  @Test
  public void test_apply_withdraw_noaccount() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long sequence = 12345;
    long amount = 100;
    TAcctMgr test = new TAcctMgr();
    WithdrawRequest deposit = new WithdrawRequest(accountName, validator, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertFalse(test.accounts.containsKey(accountName));

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_withdraw_negamount() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = -100;
    TAcctMgr test = new TAcctMgr();
    Optional<Account> oa = test.createAccount(accountName, validator, sequence, oldBalance);
    test.commitAccount(accountName);
    sequence++;
    WithdrawRequest deposit = new WithdrawRequest(accountName, validator, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);
    oa.get().commit(sequence);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName, sequence).isPresent());
    Account a = test.get(accountName, sequence).get();
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_withdraw_insufficent() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = oldBalance + 1;
    TAcctMgr test = new TAcctMgr();
    Optional<Account> oa = test.createAccount(accountName, validator, sequence, oldBalance);
    test.commitAccount(accountName);
    sequence++;
    WithdrawRequest deposit = new WithdrawRequest(accountName, validator, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);
    oa.get().commit(sequence);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName, sequence).isPresent());
    Account a = test.get(accountName, sequence).get();
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_withdraw_wrongvalidator() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    String validator2 = "validator2";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = 100;
    TAcctMgr test = new TAcctMgr();
    Optional<Account> oa = test.createAccount(accountName, validator, sequence, oldBalance);
    test.commitAccount(accountName);
    sequence++;
    WithdrawRequest deposit = new WithdrawRequest(accountName, validator2, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);
    oa.get().commit(sequence);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName, sequence).isPresent());
    Account a = test.get(accountName, sequence).get();
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_withdraw_wrongsequence() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long accountSequence = 12345;
    long reqSequence = 6834572;
    long amount = 100;
    TAcctMgr test = new TAcctMgr();
    Optional<Account> oa = test.createAccount(accountName, validator, accountSequence, oldBalance);
    test.commitAccount(accountName);
    accountSequence++;
    WithdrawRequest deposit = new WithdrawRequest(accountName, validator, amount, reqSequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);
    oa.get().commit(reqSequence);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName, accountSequence).isPresent());
    Account a = test.get(accountName, accountSequence).get();
    Assert.assertEquals(accountSequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(reqSequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }


}
