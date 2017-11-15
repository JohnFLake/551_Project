package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class DepositRequestTest {

  @Test
  public void test_toString_0cents() {
    DepositRequest test = new DepositRequest("testacct", "valid", 100l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"deposit\":1}", test.toString());
  }

  @Test
  public void test_toString_10cents() {
    DepositRequest test = new DepositRequest("testacct", "valid", 110l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"deposit\":1.1}", test.toString());
  }

  @Test
  public void test_toString_11cents() {
    DepositRequest test = new DepositRequest("testacct", "valid", 111l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"deposit\":1.11}", test.toString());
  }

  @Test
  public void test_toString_largestNumber() {
    DepositRequest test =
        new DepositRequest("testacct", "valid", AbstractRequest.maxCurrencyValue, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"deposit\":4294967295.99}", test.toString());
  }

  @Test
  public void test_apply_deposit_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = 100;
    TAcctMgr test = new TAcctMgr();
    test.createAccount(accountName, validator, sequence, oldBalance);
    sequence++;
    DepositRequest deposit = new DepositRequest(accountName, validator, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);

    // Verify
    // State is changed
    Assert.assertTrue(test.get(accountName).isPresent());
    Account a = test.get(accountName).get();
    Assert.assertEquals(sequence + 1, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(oldBalance + amount), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertTrue(t.isOk());
  }

  @Test
  public void test_apply_deposit_noaccount() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long reqSequence = 12346123;
    TAcctMgr test = new TAcctMgr();
    DepositRequest deposit = new DepositRequest(accountName, validator, 100, reqSequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertFalse(test.accounts.containsKey(accountName));

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(reqSequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_deposit_wrongsequence() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long reqSequence = 12346123;
    long acctSequence = 12345;
    TAcctMgr test = new TAcctMgr();
    test.createAccount(accountName, validator, acctSequence, oldBalance);
    acctSequence++;

    DepositRequest deposit = new DepositRequest(accountName, validator, 100, reqSequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName).isPresent());
    Account a = test.get(accountName).get();
    Assert.assertEquals(acctSequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(reqSequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_deposit_wrongvalidator() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    String validator2 = "validator2";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = 100;
    TAcctMgr test = new TAcctMgr();
    test.createAccount(accountName, validator, sequence, oldBalance);
    sequence++;
    DepositRequest deposit = new DepositRequest(accountName, validator2, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName).isPresent());
    Account a = test.get(accountName).get();
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
  public void test_apply_deposit_negamount() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long balance = 1000000;
    long sequence = 12345;
    long amount = -1;
    TAcctMgr test = new TAcctMgr();
    test.createAccount(accountName, validator, sequence, balance);
    sequence++;
    DepositRequest deposit = new DepositRequest(accountName, validator, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.get(accountName).isPresent());
    Account a = test.get(accountName).get();
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(balance), a.getBalance());

    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

}
