package edu.upenn.cis551.pncbank.bank;

import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import edu.upenn.cis551.pncbank.transaction.BalancePOJO;
import edu.upenn.cis551.pncbank.transaction.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.DepositPOJO;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;
import edu.upenn.cis551.pncbank.transaction.WithdrawPOJO;

public class AccountManagerTest {

  @Test
  public void test_apply_create_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long initialBalance = 100;
    long sequence = 12345;
    AccountManager test = new AccountManager();
    CreateAccountPOJO create =
        new CreateAccountPOJO(accountName, validator, initialBalance, sequence);

    // Run
    TransactionResponse t = test.apply(create);

    // Verify
    // State is now correct
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Account a = test.accounts.get(accountName);
    Assert.assertEquals(sequence + 1, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(initialBalance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertTrue(t.isOk());
  }

  @Test
  public void test_apply_create_duplicate() {
    // Setup
    String accountName = "test";
    String newValidator = "validator";
    String oldValidator = "validator2";
    long newInitialBalance = 100;
    long oldBalance = 1000000;
    long newSequence = 12345;
    long oldSequence = 6789123;
    AccountManager test = new AccountManager();
    Account a = new Account(oldValidator, oldSequence);
    a.updateValueAndIncrementSeq(oldBalance);
    oldSequence++;
    test.accounts.put("test", a);
    CreateAccountPOJO create =
        new CreateAccountPOJO(accountName, newValidator, newInitialBalance, newSequence);

    // Run
    TransactionResponse t = test.apply(create);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(oldSequence, a.sequence);
    Assert.assertEquals(oldValidator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(newSequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_create_negbalance() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long initialBalance = -1;
    long sequence = 12345;
    AccountManager test = new AccountManager();
    CreateAccountPOJO create =
        new CreateAccountPOJO(accountName, validator, initialBalance, sequence);

    // Run
    TransactionResponse t = test.apply(create);

    // Verify
    // State is unchanged
    Assert.assertFalse(test.accounts.containsKey(accountName));

    // Response is correct
    Assert.assertNotNull(t);
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_deposit_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = 100;
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(oldBalance);
    sequence++;
    test.accounts.put("test", a);
    DepositPOJO deposit = new DepositPOJO(accountName, validator, amount, sequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is changed
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence + 1, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance + amount), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
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
    AccountManager test = new AccountManager();
    DepositPOJO deposit = new DepositPOJO(accountName, validator, 100, reqSequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertFalse(test.accounts.containsKey(accountName));

    // Response is correct
    Assert.assertNotNull(t);
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
    long accountSequence = 12345;
    AccountManager test = new AccountManager();
    Account a = new Account(validator, accountSequence);
    a.updateValueAndIncrementSeq(oldBalance);
    accountSequence++;
    test.accounts.put("test", a);
    DepositPOJO deposit = new DepositPOJO(accountName, validator, 100, reqSequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(accountSequence, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(accountSequence, t.getSequence());// Tell the atm the right sequence number
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
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(oldBalance);
    sequence++;
    test.accounts.put("test", a);
    DepositPOJO deposit = new DepositPOJO(accountName, validator2, amount, sequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
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
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(balance);
    sequence++;
    test.accounts.put("test", a);
    DepositPOJO deposit = new DepositPOJO(accountName, validator, amount, sequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(balance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_withdraw_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long oldBalance = 1000000;
    long sequence = 12345;
    long amount = 100;
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(oldBalance);
    sequence++;
    test.accounts.put("test", a);
    WithdrawPOJO deposit = new WithdrawPOJO(accountName, validator, amount, sequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is changed
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence + 1, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance - amount), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
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
    AccountManager test = new AccountManager();
    WithdrawPOJO deposit = new WithdrawPOJO(accountName, validator, amount, sequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertFalse(test.accounts.containsKey(accountName));

    // Response is correct
    Assert.assertNotNull(t);
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
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(oldBalance);
    sequence++;
    test.accounts.put("test", a);
    WithdrawPOJO deposit = new WithdrawPOJO(accountName, validator, amount, sequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
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
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(oldBalance);
    sequence++;
    test.accounts.put("test", a);
    WithdrawPOJO deposit = new WithdrawPOJO(accountName, validator, amount, sequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
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
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(oldBalance);
    sequence++;
    test.accounts.put("test", a);
    WithdrawPOJO deposit = new WithdrawPOJO(accountName, validator2, amount, sequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
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
    AccountManager test = new AccountManager();
    Account a = new Account(validator, accountSequence);
    a.updateValueAndIncrementSeq(oldBalance);
    accountSequence++;
    test.accounts.put("test", a);
    WithdrawPOJO deposit = new WithdrawPOJO(accountName, validator, amount, reqSequence);

    // Run
    TransactionResponse t = test.apply(deposit);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(accountSequence, a.sequence);
    Assert.assertEquals(validator, a.cardValidator);
    Assert.assertEquals(BigInteger.valueOf(oldBalance), a.balance);

    // Response is correct
    Assert.assertNotNull(t);
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(accountSequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

  @Test
  public void test_apply_balance_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long balance = 1000000;
    long sequence = 12345;
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(balance);
    sequence++;
    test.accounts.put("test", a);
    BalancePOJO bal = new BalancePOJO(accountName, validator, sequence);

    // Run
    TransactionResponse t = test.apply(bal);

    // Verify
    // State is changed (only sequence number)
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence + 1, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(balance), a.balance);

    // Response is correct
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
    AccountManager test = new AccountManager();
    BalancePOJO bal = new BalancePOJO(accountName, validator, sequence);

    // Run
    TransactionResponse t = test.apply(bal);

    // Verify
    // State is unchanged
    Assert.assertFalse(test.accounts.containsKey(accountName));

    // Response is correct
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
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(balance);
    sequence++;
    test.accounts.put("test", a);
    BalancePOJO bal = new BalancePOJO(accountName, validator, reqSequence);

    // Run
    TransactionResponse t = test.apply(bal);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(balance), a.balance);

    // Response is correct
    Assert.assertFalse(t instanceof BalanceResponse);
    Assert.assertFalse(t.isOk());
    Assert.assertEquals(sequence, t.getSequence()); // Tells the atm the correct sequence number
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
    AccountManager test = new AccountManager();
    Account a = new Account(validator, sequence);
    a.updateValueAndIncrementSeq(balance);
    sequence++;
    test.accounts.put("test", a);
    BalancePOJO bal = new BalancePOJO(accountName, validator2, sequence);

    // Run
    TransactionResponse t = test.apply(bal);

    // Verify
    // State is unchanged
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Assert.assertEquals(sequence, a.getSequence());
    Assert.assertEquals(validator, a.getCardValidator());
    Assert.assertEquals(BigInteger.valueOf(balance), a.balance);

    // Response is correct
    Assert.assertFalse(t instanceof BalanceResponse);
    Assert.assertFalse(t.isOk());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertEquals(accountName, t.getAccountId());
  }
}
