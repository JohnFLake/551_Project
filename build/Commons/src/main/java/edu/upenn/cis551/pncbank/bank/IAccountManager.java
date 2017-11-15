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
   * Gets the account for this account name
   * 
   * @param accountName
   * @return
   */
  Optional<Account> get(String accountName);

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

}
