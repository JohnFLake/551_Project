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
    account.ifPresent(a -> {
      StringBuilder sb = new StringBuilder();
      sb.append('{');
      sb.append("\"balance\":").append(PrintUtils.writeCurrency(a.getBalance().toString()))
          .append(',');
      sb.append("\"account\":\"").append(this.getAccountName()).append("\"}");
      System.out.println(sb.toString());
      System.out.flush();
    });
  }
}
