package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.IAccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;
import edu.upenn.cis551.pncbank.utils.PrintUtils;

public class WithdrawRequest extends AbstractRequest {

  long withdraw;
  String validation;

  /**
   * Private constructor for use with Jackson.
   */
  private WithdrawRequest() {
    super(null, 0);
  }

  public WithdrawRequest(String accountName, String validation, long amount, long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.withdraw = amount;
    this.validation = validation;
  }

  /**
   * @return the amount
   */
  public final long getWithdraw() {
    return this.withdraw;
  }

  public final String getValidation() {
    return this.validation;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    sb.append("\"account\":\"").append(this.getAccountName()).append("\",");
    sb.append("\"withdraw\":").append(PrintUtils.writeCurrency(this.getWithdraw()));
    sb.append('}');
    return sb.toString();
  }

  @Override
  public Optional<TransactionResponse> apply(String accountName, IAccountManager am) {
    return Optional.of(am.get(accountName)
        .filter(a -> a.getCardValidator().equals(this.getValidation()))
        .filter(a -> a.getSequence() == this.getSequenceNumber())
        .filter(a -> this.getWithdraw() > 0)
        // updateValue checks that the balance can't be made negative.
        .map(a -> new TransactionResponse(a.updateValueAndIncrementSeq(-1 * this.getWithdraw()),
            this.getAccountName(), this.getSequenceNumber()))
        .orElse(new TransactionResponse(false, this.getAccountName(), this.getSequenceNumber())));
  }
}
