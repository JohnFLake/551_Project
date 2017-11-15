package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.IAccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;
import edu.upenn.cis551.pncbank.utils.PrintUtils;

public class DepositRequest extends AbstractRequest {
  long deposit;
  String validation;

  /**
   * Private constructor for use with Jackson.
   */
  private DepositRequest() {
    super(null, 0);
  }

  public DepositRequest(String accountName, String validation, long amount, long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.deposit = amount;
    this.validation = validation;
  }

  public final long getDeposit() {
    return this.deposit;
  }

  public final String getValidation() {
    return this.validation;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    sb.append("\"account\":\"").append(this.getAccountName()).append("\",");
    sb.append("\"deposit\":").append(PrintUtils.writeCurrency(this.getDeposit()));
    sb.append('}');
    return sb.toString();
  }

  @Override
  public Optional<TransactionResponse> apply(String accountName, IAccountManager am) {
    return Optional.of(am.get(accountName)
        .filter(a -> a.getCardValidator().equals(this.getValidation()))
        .filter(a -> a.getSequence() == this.getSequenceNumber()).filter(a -> this.getDeposit() > 0)
        .map(a -> new TransactionResponse(a.updateValueAndIncrementSeq(deposit),
            this.getAccountName(), this.getSequenceNumber()))
        .orElse(new TransactionResponse(false, this.getAccountName(), this.getSequenceNumber())));
  }
}
