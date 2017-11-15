package edu.upenn.cis551.pncbank.bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import edu.upenn.cis551.pncbank.transaction.request.AbstractRequest;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class AccountManager implements IAccountManager {
  Map<String, Account> accounts;

  public AccountManager() {
    this.accounts = new HashMap<String, Account>();
  }

  @Override
  public Optional<Account> get(String name) {
    return Optional.ofNullable(this.accounts.get(name));
  }

  /**
   * Attempts to apply a transaction and builds a response.
   * 
   * @param t The transaction to apply.
   * @return A TransactionResponse to return to the request originator. Uses the same sequence
   *         number as the request.
   */
  public Optional<TransactionResponse> apply(AbstractRequest t) {
    return t.apply(this);
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
      result.updateValueAndIncrementSeq(balance);
      this.accounts.put(accountId, result);
      return Optional.of(result);
    }
  }
}
