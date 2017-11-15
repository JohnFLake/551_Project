package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.bank.AccountManager;
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
  public Optional<TransactionResponse> apply(AccountManager am) {
    Optional<Account> account = am.get(this.getAccountName(), this.getSequenceNumber());
    TransactionResponse r = account.filter(a -> a.getCardValidator().equals(this.getValidation()))
        .filter(a -> a.getSequence() == this.getSequenceNumber()).map(a -> {
          return (TransactionResponse) new BalanceResponse(true, this.getAccountName(),
              this.getSequenceNumber(), PrintUtils.writeCurrency(a.getBalance().toString()));
        }).orElse(new TransactionResponse(false, this.getAccountName(), this.getSequenceNumber()));

    if (r.isOk()) {
      account.get().defer(this);
    }

    return Optional.of(r);
  }

  @Override
  public void commit(Optional<Account> account) {
    // Nothing needs to be done
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof BalanceRequest)) {
      return false;
    }
    BalanceRequest r = (BalanceRequest) other;
    return Optional.ofNullable(r.getAccountName())
        .filter(name -> name.equals(this.getAccountName())).isPresent()
        && r.getSequenceNumber() == this.getSequenceNumber()
        && Optional.ofNullable(r.getValidation()).filter(val -> val.equals(this.getValidation()))
            .isPresent();
  }
}
