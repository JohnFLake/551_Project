package edu.upenn.cis551.pncbank.bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    if (t instanceof CreateAccountPOJO) {
      CreateAccountPOJO request = (CreateAccountPOJO) t;
      return createAccount(accountId, request.getValidator(), request.getSequenceNumber(),
          request.getBalance())
              .map(acct -> new TransactionResponse(true, accountId, request.getSequenceNumber()))
              .orElse(new TransactionResponse(false, accountId, request.getSequenceNumber()));

    } else if (t instanceof DepositPOJO) {
      DepositPOJO request = (DepositPOJO) t;
      Account a = this.accounts.get(accountId);

      if (null == a || !a.getCardValidator().equals(request.getValidation())) {
        return new TransactionResponse(false, accountId, request.getSequenceNumber());
      }
      long deposit = request.getDeposit();
      if (a.getSequence() != request.getSequenceNumber() || deposit < 0) {
        // Tell the atm the next sequence number only if validation passed.
        return new TransactionResponse(false, accountId, a.getSequence());
      }

      return new TransactionResponse(a.updateValueAndIncrementSeq(deposit), accountId,
          request.getSequenceNumber());

    } else if (t instanceof WithdrawPOJO) {
      WithdrawPOJO request = (WithdrawPOJO) t;
      Account a = this.accounts.get(accountId);

      if (null == a || !a.getCardValidator().equals(request.getValidation())) {
        return new TransactionResponse(false, accountId, request.getSequenceNumber());
      }

      long withdrawl = request.getWithdraw();
      if (a.getSequence() != request.getSequenceNumber() || withdrawl < 0) {
        return new TransactionResponse(false, accountId, a.getSequence());
      }
      return new TransactionResponse(a.updateValueAndIncrementSeq(-1 * withdrawl), accountId,
          request.getSequenceNumber());
    } else if (t instanceof BalancePOJO) {
      BalancePOJO request = (BalancePOJO) t;
      Account a = this.accounts.get(accountId);

      if (null == a || !a.getCardValidator().equals(request.getValidation())) {
        return new TransactionResponse(false, accountId, request.getSequenceNumber());
      }

      if (a.getSequence() != request.getSequenceNumber()) {
        return new TransactionResponse(false, accountId, a.getSequence());
      }

      return new BalanceResponse(true, accountId, request.getSequenceNumber(),
          a.readValueTransaction());
    }
    return null;
  }

  /**
   * Creates a new account with the provided information. Fails if there is another account with
   * that name.
   * 
   * @param accountId
   * @param validator
   * @param sequence The sequence number given in the create request
   * @param balance
   * @return An Optional with an account. One will be present if creating an account succeeds.
   */
  public synchronized Optional<Account> createAccount(String accountId, String validator,
      long sequence, long balance) {
    if (this.accounts.containsKey(accountId) || balance < 0l || null == validator) {
      return Optional.empty();
    } else {
      Account result = new Account(validator, sequence);
      result.updateValueAndIncrementSeq(balance);
      this.accounts.put(accountId, result);
      return Optional.of(result);
    }
  }

  public boolean changeValue(Account a, String validation, long delta, long sequence) {
    if (null == a || !checkSequenceValidation(a, validation, sequence)) {
      return false;
    }
    return a.updateValueAndIncrementSeq(delta);
  }

  public boolean checkSequenceValidation(Account a, String validation, long sequence) {
    return a.getCardValidator() == validation && a.getSequence() == sequence;
  }
}
