package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.IAccountManager;
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
  public Optional<TransactionResponse> apply(IAccountManager am) {
    return Optional.of(am
        .createAccount(this.getAccountName(), this.getValidator(), this.getSequenceNumber(),
            this.getBalance())
        .map(acct -> new TransactionResponse(true, this.getAccountName(), this.getSequenceNumber()))
        .orElse(new TransactionResponse(false, this.getAccountName(), this.getSequenceNumber())));
  }

}
