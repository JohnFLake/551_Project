package edu.upenn.cis551.pncbank.bank;

import java.util.Optional;
import edu.upenn.cis551.pncbank.transaction.request.AbstractRequest;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

public interface IAccountManager {
  /**
   * Applies a given transaction to the account manager
   * 
   * @param t The transaction to apply
   * @return A transaction response
   */
  Optional<TransactionResponse> apply(AbstractRequest t);

  /**
   * Gets the account for this account name. If the account is pending and the sequence number is
   * the next one as kept in the pending account, the account is committed.
   * 
   * @param accountName
   * @param s The sequence number
   * @return An optional containing the matching account.
   */
  Optional<Account> get(String accountName, long s);

  /**
   * Creates a new account in the manager iff no such account already exists.
   * 
   * @param accountName
   * @param validator
   * @param sequenceNumber
   * @param balance
   * @return An empty optional if a conflicting account exists. Otherwise it will include the
   *         account.
   */
  Optional<Account> createAccount(String accountName, String validator, long sequenceNumber,
      long balance);

  /**
   * Commits an account after an acknowledgement.
   * 
   * @param accountName
   * @return
   */
  public boolean commitAccount(String accountName);

}
