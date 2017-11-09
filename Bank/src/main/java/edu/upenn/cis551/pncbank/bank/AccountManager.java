package edu.upenn.cis551.pncbank.bank;

import java.util.HashMap;
import java.util.Map;
import edu.upenn.cis551.pncbank.transaction.AbstractTransaction;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.DepositPOJO;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;
import edu.upenn.cis551.pncbank.transaction.WithdrawPOJO;

public class AccountManager {
  Map<String, Account> accounts;

  public AccountManager() {
    this.accounts = new HashMap<String, Account>();
  }

  public TransactionResponse apply(AbstractTransaction t) {
    String accountId = t.getAccountName();
    long sequence = t.getSequenceNumber();
    long responseSequence = sequence + 1;
    if (t instanceof CreateAccountPOJO) {
      CreateAccountPOJO request = (CreateAccountPOJO) t;
      long balance = request.getBalance();
      String validator = request.getValidator();
      return new TransactionResponse(createAccount(accountId, validator, sequence, balance),
          accountId, responseSequence);
    } else if (t instanceof DepositPOJO) {
      Account a = this.accounts.get(accountId);
      if (null == a) {
        return new TransactionResponse(false, accountId, responseSequence);
      }
      DepositPOJO request = (DepositPOJO) t;
      long deposit = request.getDeposit();
      String validation = request.getValidation();
      return new TransactionResponse(changeValue(accountId, validation, deposit, sequence),
          accountId, responseSequence);

    } else if (t instanceof WithdrawPOJO) {
      Account a = this.accounts.get(accountId);
      if (null == a) {
        return new TransactionResponse(false, accountId, responseSequence);
      }
      WithdrawPOJO request = (WithdrawPOJO) t;
      String validation = request.getValidation();
      long withdrawl = request.getWithdraw();
      return new TransactionResponse(changeValue(accountId, validation, -1 * withdrawl, sequence),
          accountId, responseSequence);
    }
    return null;
  }

  /**
   * Creates a new account with the provided information. Fails if there is another account with
   * that name.
   * 
   * @param accountId
   * @param validator
   * @param sequence
   * @param balance
   * @return <code>true</code> unless an account exists with that name.
   */
  public synchronized boolean createAccount(String accountId, String validator, long sequence,
      long balance) {
    if (this.accounts.containsKey(accountId)) {
      return false;
    } else {
      this.accounts.put(accountId, new Account(balance, validator, sequence));
      return true;
    }
  }

  public boolean changeValue(String accountId, String validation, long delta, long sequence) {
    Account account = this.accounts.get(accountId);
    if (null == account) {
      return false;
    }
    return account.updateValue(delta, validation, sequence);
  }
}
