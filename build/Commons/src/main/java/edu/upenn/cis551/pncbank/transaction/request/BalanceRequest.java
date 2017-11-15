package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.IAccountManager;
import edu.upenn.cis551.pncbank.transaction.response.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;
import edu.upenn.cis551.pncbank.utils.PrintUtils;

public class BalanceRequest extends AbstractRequest {
  String validation;

  /**
   * Private constructor for use with Jackson.
   */
  private BalanceRequest() {
    super(null, 0);
  }

  public BalanceRequest(String accountName, String validation, long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.validation = validation;
  }

  /**
   * @return the validation
   */
  public final String getValidation() {
    return this.validation;
  }

  @Override
  public Optional<TransactionResponse> apply(IAccountManager am) {
    return Optional.of(am.get(this.getAccountName())
        .filter(a -> a.getCardValidator().equals(this.getValidation()))
        .filter(a -> a.getSequence() == this.getSequenceNumber())
        .map(a -> (TransactionResponse) new BalanceResponse(true, this.getAccountName(),
            this.getSequenceNumber(), PrintUtils.writeCurrency(a.readValueTransaction())))
        .orElse(new TransactionResponse(false, this.getAccountName(), this.getSequenceNumber())));
  }
}
