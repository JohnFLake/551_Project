package edu.upenn.cis551.pncbank.bank;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import edu.upenn.cis551.pncbank.transaction.request.AbstractRequest;
import edu.upenn.cis551.pncbank.transaction.request.CreateAccountRequest;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class AccountManager implements IAccountManager {
  Map<String, Account> accounts;

  private Map<String, Account> pending;

  public AccountManager() {
    this.accounts = new HashMap<>();
    this.pending = new HashMap<>();
  }

  @Override
  public Optional<Account> get(String accountName, long s) {
    if (this.pending.containsKey(accounts)
        && this.pending.get(accountName).getSequence() == s - 1) {
      this.commitAccount(accountName);
    }
    return Optional.ofNullable(this.accounts.get(accountName)).filter(a -> a.getSequence() == s);
  }

  /**
   * Attempts to apply a transaction and builds a response.
   * 
   * @param t The transaction to apply.
   * @return A TransactionResponse to return to the request originator. Uses the same sequence
   *         number as the request.
   */
  public synchronized Optional<TransactionResponse> apply(AbstractRequest t) {
    return t.apply(this);
  }

  public synchronized boolean commitAccount(String accountName) {
    Account a = this.pending.get(accountName);
    if (null != a) {
      this.pending.remove(accountName);
      this.accounts.put(accountName, a);
      System.out.println(new CreateAccountRequest(accountName, a.getCardValidator(),
          a.getBalance().longValue(), a.getSequence()).toString());
      a.sequence++;
      return true;
    }
    return false;
  }

  /**
   * Creates a new account with the provided information. Fails if there is another account with
   * that name.
   * 
   * @param accountId The id of the account.
   * @param validator The validator to use for the account.
   * @param sequence The sequence number given in the create request.
   * @param balance The initial account balance.
   * @return An Optional with an account. One will be present if creating an account succeeds.
   */
  public synchronized Optional<Account> createAccount(String accountId, String validator,
      long sequence, long balance) {
    if (this.accounts.containsKey(accountId) || balance < 1000l || null == validator) {
      return Optional.empty();
    } else {
      Account result = new Account(validator, sequence);
      result.setBalance(BigInteger.valueOf(balance));
      this.pending.put(accountId, result);
      return Optional.of(result);
    }
  }
}
