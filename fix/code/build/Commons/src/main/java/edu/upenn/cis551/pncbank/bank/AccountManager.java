package edu.upenn.cis551.pncbank.bank;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import edu.upenn.cis551.pncbank.transaction.request.AbstractRequest;
import edu.upenn.cis551.pncbank.transaction.request.CreateAccountRequest;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public class AccountManager {
  Map<String, Account> accounts;

  private Map<String, Account> pending;

  public AccountManager() {
    this.accounts = new HashMap<>();
    this.pending = new HashMap<>();
  }

  /**
   * @param accountName
   * @return An Optional containing the account matching that name.
   */
  public Optional<Account> get(String accountName) {
    return Optional.ofNullable(this.accounts.get(accountName));
  }

  /**
   * Gets an account iff it exists and the sequence number matches. If the account is pending with
   * the previous sequence number, the account is first committed. If a transaction is pending with
   * the previous sequence number on the account, commits the previous transaction and then
   * succeeds.
   * 
   * @param accountName
   * @param s
   * @return An Optional containing the matching account.
   */
  // WARNING!!! This method has a number of side effects and should not be used in tests.
  public Optional<Account> get(String accountName, long s) {
    // If something is asking for the pending account with the previous sequence number, it must
    // have thought the creation succeeded and sent an ack. Finish the pending call.
    if (this.pending.containsKey(accountName)
        && this.pending.get(accountName).getSequence() == s - 1) {
      this.commitAccount(accountName);
    }
    Account a = this.accounts.get(accountName);
    return Optional.ofNullable(a).filter(acct -> {
      if (null != acct.pending && acct.pending.getSequenceNumber() == s - 1) {
        acct.commit(acct.pending); // First commit any pending transactions with the previous
                                   // sequence number, as this may change the account's sequence
                                   // number expectation.
        // This is safe since the only way a request can come in with the next sequence number is if
        // the atm got an ok response to the previous one.
      }
      return acct.getSequence() == s;
    });
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

  /**
   * Commits an account after an acknowledgement.
   * 
   * @param accountName
   * @return
   */
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

  public boolean isPending(String accountId) {
    return this.pending.containsKey(accountId);
  }
}
