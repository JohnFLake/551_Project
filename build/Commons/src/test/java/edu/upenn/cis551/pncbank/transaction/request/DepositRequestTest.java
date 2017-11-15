package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class DepositRequestTest {

  AccountManager am;

  @Before
  public void setup() {
    am = new AccountManager();
  }

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
    Optional<Account> oa = am.createAccount(accountName, validator, sequence, oldBalance);
    am.commitAccount(accountName);
    sequence++;
    DepositRequest deposit = new DepositRequest(accountName, validator, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = am.apply(deposit);
    // Ack
    oa.ifPresent(a -> a.commit(deposit.getSequenceNumber()));

    // Verify
    // State is changed
    oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
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
  public void test_apply_deposit_uncommittedaccount() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = 100;
    Optional<Account> oa = am.createAccount(accountName, validator, sequence, oldBalance);
    sequence++;
    DepositRequest deposit = new DepositRequest(accountName, validator, amount, sequence);

    // Run
    Optional<TransactionResponse> ot = am.apply(deposit);
    // Ack
    oa.ifPresent(a -> a.commit(deposit.getSequenceNumber()));

    // Verify
    // State is changed
    oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
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
    DepositRequest deposit = new DepositRequest(accountName, validator, 100, reqSequence);

    // Run
    Optional<TransactionResponse> ot = am.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertFalse(am.isPending(accountName) || am.get(accountName).isPresent());

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
    Optional<Account> oa = am.createAccount(accountName, validator, acctSequence, oldBalance);
    am.commitAccount(accountName);
    acctSequence++;

    DepositRequest deposit = new DepositRequest(accountName, validator, 100, reqSequence);
    oa.ifPresent(a -> a.commit(deposit.getSequenceNumber()));

    // Run
    Optional<TransactionResponse> ot = am.apply(deposit);

    // Verify
    // State is unchanged
    oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
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
    Optional<Account> oa = am.createAccount(accountName, validator, sequence, oldBalance);
    am.commitAccount(accountName);
    sequence++;
    DepositRequest deposit = new DepositRequest(accountName, validator2, amount, sequence);
    oa.ifPresent(a -> a.commit(deposit.getSequenceNumber()));

    // Run
    Optional<TransactionResponse> ot = am.apply(deposit);

    // Verify
    // State is unchanged
    oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
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
    Optional<Account> oa = am.createAccount(accountName, validator, sequence, balance);
    am.commitAccount(accountName);
    sequence++;
    DepositRequest deposit = new DepositRequest(accountName, validator, amount, sequence);
    oa.ifPresent(a -> a.commit(deposit.getSequenceNumber()));

    // Run
    Optional<TransactionResponse> ot = am.apply(deposit);

    // Verify
    // State is unchanged
    oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
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
