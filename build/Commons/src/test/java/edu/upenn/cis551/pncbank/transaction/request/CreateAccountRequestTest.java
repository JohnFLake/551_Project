package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class CreateAccountRequestTest {

  @Test
  public void test_apply_create_ok() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long initialBalance = 100;
    long sequence = 12345;
    TAcctMgr test = new TAcctMgr();
    CreateAccountRequest create =
        new CreateAccountRequest(accountName, validator, initialBalance, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(create);
    test.commitAccount(accountName);

    // Verify
    // State is now correct
    Assert.assertTrue(test.accounts.containsKey(accountName));
    Account a = test.accounts.get(accountName);
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
  public void test_apply_create_fail() {
    // Setup
    String accountName = "test";
    String validator = "validator";
    long initialBalance = -1;
    long sequence = 12345;
    TAcctMgr test = new TAcctMgr() {
      @Override
      public Optional<Account> createAccount(String accountId, String validator, long sequence,
          long balance) {
        // Fails for some reason
        return Optional.empty();
      }
    };
    CreateAccountRequest create =
        new CreateAccountRequest(accountName, validator, initialBalance, sequence);

    // Run
    Optional<TransactionResponse> ot = test.apply(create);

    // Verify
    // Response is correct
    Assert.assertTrue(ot.isPresent());
    TransactionResponse t = ot.get();
    Assert.assertEquals(accountName, t.getAccountId());
    Assert.assertEquals(sequence, t.getSequence());
    Assert.assertFalse(t.isOk());
  }

}
