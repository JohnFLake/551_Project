package edu.upenn.cis551.pncbank.bank;

import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import edu.upenn.cis551.pncbank.transaction.request.DepositRequest;

public class AccountManagerTest {

  AccountManager am;

  @Before
  public void setup() {
    am = new AccountManager();
  }

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
    Optional<Account> oa1 = am.createAccount(accountName, validator, sequence, amount);
    Optional<Account> oa2 = am.createAccount(accountName2, validator2, sequence2, amount2);
    am.commitAccount(accountName);
    am.commitAccount(accountName2);

    Assert.assertTrue(oa1.isPresent());
    Assert.assertTrue(oa2.isPresent());

    oa1 = am.get(accountName);
    Account a1 = oa1.get();
    Assert.assertEquals(a1.getCardValidator(), validator);
    Assert.assertEquals(a1.getSequence(), sequence + 1);
    Assert.assertEquals(a1.getBalance().longValue(), amount);
    Assert.assertNull(a1.pending);

    oa2 = am.get(accountName2);
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
    long sequence1 = 12345;
    long sequence2 = -1234;
    long amount = 100000;
    Optional<Account> oa1 = am.createAccount(accountName, validator, sequence1, amount);
    am.commitAccount(accountName);
    sequence1++;
    Optional<Account> oa2 = am.createAccount(accountName, validator, sequence2, amount);
    am.commitAccount(accountName);
    sequence2++;

    Assert.assertTrue(oa1.isPresent());
    Assert.assertFalse(oa2.isPresent());

    Optional<Account> oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
    Assert.assertEquals(a.getCardValidator(), validator);
    Assert.assertEquals(a.getSequence(), sequence1);
    Assert.assertEquals(a.getBalance().longValue(), amount);
    Assert.assertNull(a.pending);

  }

  @Test
  public void test_createAccount_duplicate_uncommitted() {
    String accountName = "test";
    String validator = "validator";
    String validator2 = "validator2";
    long sequence = 12345;
    long amount = 100000;
    long sequence2 = 123456;
    long amount2 = 1000;
    Optional<Account> oa1 = am.createAccount(accountName, validator, sequence, amount);
    Optional<Account> oa2 = am.createAccount(accountName, validator2, sequence2, amount2);
    am.commitAccount(accountName);
    sequence2++;

    Assert.assertTrue(oa1.isPresent());
    Assert.assertTrue(oa2.isPresent());

    Optional<Account> oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
    Assert.assertEquals(a.getCardValidator(), validator2);
    Assert.assertEquals(a.getSequence(), sequence2);
    Assert.assertEquals(a.getBalance().longValue(), amount2);
    Assert.assertNull(a.pending);

  }

  @Test
  public void test_get_pendingAccount_nextSequence() {
    String accountName = "test";
    String validator = "validator";
    long sequence = 12345;
    long amount = 100000;
    Optional<Account> oa1 = am.createAccount(accountName, validator, sequence, amount);

    Assert.assertTrue(oa1.isPresent());
    Assert.assertTrue(am.isPending(accountName));

    // Run
    Optional<Account> oa = am.get(accountName, sequence + 1);

    // Verify
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get();
    Assert.assertEquals(a.getCardValidator(), validator);
    Assert.assertEquals(a.getSequence(), sequence + 1);
    Assert.assertEquals(a.getBalance().longValue(), amount);
    Assert.assertFalse(am.isPending(accountName));
    Assert.assertNull(a.pending);
  }

  @Test
  public void test_get_pendingRequest_nextSequence() {
    String accountName = "test";
    String validator = "v";
    long sequence = 76543l;
    long startAmt = 10000l;
    long v = 10000l;

    Optional<Account> oa = am.createAccount(accountName, validator, sequence, startAmt);
    am.commitAccount(accountName);
    Assert.assertTrue(am.get(accountName).isPresent());
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get(); // The account
    sequence++; // From account commit.
    DepositRequest deposit1 = new DepositRequest(accountName, validator, v, sequence);

    am.apply(deposit1);
    // No commit (due to intercept of ack)
    // Atm has updated the sequence
    sequence++;
    DepositRequest deposit2 = new DepositRequest(accountName, validator, v, sequence);

    // Run
    am.apply(deposit2);
    a.commit(deposit2);
    sequence++;

    // Verify
    oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    a = oa.get();

    Assert.assertEquals(startAmt + v + v, a.getBalance().longValueExact());
    Assert.assertNull(a.pending);
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());

  }

  @Test
  public void test_get_pendingRequest_sameSequence() {
    String accountName = "test";
    String validator = "v";
    long sequence = 76543l;
    long startAmt = 10000l;
    long v = 10000l;

    Optional<Account> oa = am.createAccount(accountName, validator, sequence, startAmt);
    am.commitAccount(accountName);
    Assert.assertTrue(am.get(accountName).isPresent());
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get(); // The account
    sequence++; // From account commit.
    DepositRequest deposit1 = new DepositRequest(accountName, validator, v, sequence);

    am.apply(deposit1);
    // No commit (due to intercept of ack)
    // Atm has not updated the sequence
    DepositRequest deposit2 = new DepositRequest(accountName, validator, v, sequence);

    // Run
    am.apply(deposit2);
    a.commit(deposit2);
    sequence++;

    // Verify
    oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    a = oa.get();

    Assert.assertEquals(startAmt + v, a.getBalance().longValueExact());
    Assert.assertNull(a.pending);
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
  }

  @Test
  public void test_get_pendingRequest_otherSequence() {
    String accountName = "test";
    String validator = "v";
    long sequence = 76543l;
    long startAmt = 10000l;
    long v1 = 10000l;
    long v2 = 1000000l;

    Optional<Account> oa = am.createAccount(accountName, validator, sequence, startAmt);
    am.commitAccount(accountName);
    Assert.assertTrue(am.get(accountName).isPresent());
    Assert.assertTrue(oa.isPresent());
    Account a = oa.get(); // The account
    sequence++; // From account commit.
    DepositRequest deposit1 = new DepositRequest(accountName, validator, v1, sequence);

    am.apply(deposit1);
    // No commit (due to intercept of ack)
    Assert.assertTrue(null != a.pending && a.pending.equals(deposit1));
    // Atm sends garbage sequence
    DepositRequest deposit2 = new DepositRequest(accountName, validator, v2, sequence - 6000l);

    // Run
    am.apply(deposit2);

    // Verify
    oa = am.get(accountName);
    Assert.assertTrue(oa.isPresent());
    a = oa.get();

    Assert.assertEquals(startAmt, a.getBalance().longValueExact());
    Assert.assertNotNull(a.pending);
    Assert.assertEquals(deposit1, a.pending);
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
  }

  @Test
  public void test_get_pendingAccount_diffSequence() {
    String accountName = "test";
    String validator = "validator";
    long sequence = 12345;
    long amount = 100000;
    Optional<Account> oa1 = am.createAccount(accountName, validator, sequence, amount);

    Assert.assertTrue(oa1.isPresent());
    Assert.assertTrue(am.isPending(accountName));

    // Run
    Optional<Account> oa = am.get(accountName, sequence);

    // Verify
    Assert.assertFalse(oa.isPresent());
    Assert.assertTrue(am.isPending(accountName));
  }
}
