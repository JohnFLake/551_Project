package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;
import edu.upenn.cis551.pncbank.utils.PrintUtils;

public class CreateAccountRequest extends AbstractRequest {
  long balance;
  String validator;

  /**
   * Private constructor for use with Jackson.
   */
  private CreateAccountRequest() {
    super(null, 0);
  }

  public CreateAccountRequest(String accountName, String validator, long balance,
      long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.balance = balance;
    this.validator = validator;
  }

  public long getBalance() {
    return this.balance;
  }

  public String getValidator() {
    return this.validator;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    sb.append("\"account\":\"").append(this.getAccountName()).append("\",");
    sb.append("\"initial_balance\":").append(PrintUtils.writeCurrency(this.getBalance()));
    sb.append('}');
    return sb.toString();
  }

  @Override
  public Optional<TransactionResponse> apply(AccountManager am) {
    TransactionResponse r = am.createAccount(this.getAccountName(), this.getValidator(),
        this.getSequenceNumber(), this.getBalance()).map(acct -> {
          return new TransactionResponse(true, this.getAccountName(), this.getSequenceNumber());
        }).orElseGet(() -> {
          return new TransactionResponse(false, this.getAccountName(), this.getSequenceNumber());
        });
    return Optional.of(r);
  }

  @Override
  public void commit(Optional<Account> account) {
    // Shouldn't be called
  }

}
