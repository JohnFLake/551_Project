package edu.upenn.cis551.pncbank.bank;

import java.util.HashMap;
import java.util.Map;
import edu.upenn.cis551.pncbank.transaction.AbstractTransaction;
import edu.upenn.cis551.pncbank.transaction.BalancePOJO;
import edu.upenn.cis551.pncbank.transaction.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.DepositPOJO;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;
import edu.upenn.cis551.pncbank.transaction.WithdrawPOJO;

public class AccountManager {
  Map<String, Account> accounts;

  public AccountManager() {
    this.accounts = new HashMap<String, Account>();
  }

  /**
   * Attempts to apply a transaction and builds a response.
   * 
   * @param t The transaction to apply.
   * @return A TransactionResponse to return to the request originator. Uses the same sequence
   *         number as the request.
   */
  public TransactionResponse apply(AbstractTransaction t) {
    String accountId = t.getAccountName();
    long sequence = t.getSequenceNumber();

    if (t instanceof CreateAccountPOJO) {
      CreateAccountPOJO request = (CreateAccountPOJO) t;
      long balance = request.getBalance();
      String validator = request.getValidator();
      return new TransactionResponse(createAccount(accountId, validator,
          // sequence + 1 since the next request will be with the next number.
          sequence + 1, balance), accountId, sequence);

    } else if (t instanceof DepositPOJO) {
      Account a = this.accounts.get(accountId);
      if (null == a) {
        return new TransactionResponse(false, accountId, sequence);
      }
      DepositPOJO request = (DepositPOJO) t;
      long deposit = request.getDeposit();
      if (deposit < 0) {
        return new TransactionResponse(false, accountId, sequence);
      }
      String validation = request.getValidation();
      return new TransactionResponse(changeValue(a, validation, deposit, sequence),
          accountId, sequence);

    } else if (t instanceof WithdrawPOJO) {
      Account a = this.accounts.get(accountId);
      if (null == a) {
        return new TransactionResponse(false, accountId, sequence);
      }
      WithdrawPOJO request = (WithdrawPOJO) t;
      String validation = request.getValidation();
      long withdrawl = request.getWithdraw();
      if (withdrawl < 0) {
        return new TransactionResponse(false, accountId, sequence);
      }
      return new TransactionResponse(changeValue(a, validation, -1 * withdrawl, sequence),
          accountId, sequence);
    } else if (t instanceof BalancePOJO) {
      Account a = this.accounts.get(accountId);
      if (null == a) {
        return new TransactionResponse(false, accountId, sequence);
      }
      BalancePOJO request = (BalancePOJO) t;
      String validation = request.getValidation();
      if(!checkSequenceValidation(a, validation, sequence)) {
        return new TransactionResponse(false, accountId, sequence);
      }
      return new BalanceResponse(true, accountId, sequence, a.readValue());
    }
    return null;
  }

  /**
   * Creates a new account with the provided information. Fails if there is another account with
   * that name.
   * 
   * @param accountId
   * @param validator
   * @param nextSequence The next sequence number that this account should expect
   * @param balance
   * @return <code>true</code> unless an account exists with that name.
   */
  public synchronized boolean createAccount(String accountId, String validator, long nextSequence,
      long balance) {
    if (this.accounts.containsKey(accountId) || balance < 0l) {
      return false;
    } else {
      this.accounts.put(accountId, new Account(balance, validator, nextSequence));
      return true;
    }
  }

  public boolean changeValue(Account a, String validation, long delta, long sequence) {
    if (null == a || !checkSequenceValidation(a, validation, sequence)) {
      return false;
    }
    return a.updateValue(delta);
  }

  public boolean checkSequenceValidation(Account a, String validation, long sequence) {
    return a.getCardValidator() == validation && a.getSequence() == sequence;
  }
}
