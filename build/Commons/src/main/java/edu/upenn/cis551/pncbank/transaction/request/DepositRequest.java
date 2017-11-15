package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.Account;
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
  public Optional<TransactionResponse> apply(IAccountManager am) {
    Optional<Account> oa = am.get(this.getAccountName(), this.getSequenceNumber());
    TransactionResponse r = oa.filter(a -> a.getCardValidator().equals(this.getValidation()))
        .filter(a -> a.getSequence() == this.getSequenceNumber())//
        .filter(a -> this.getDeposit() > 0) //
        .map(a -> {
          return new TransactionResponse(true, this.getAccountName(), this.getSequenceNumber());
        }).orElseGet(() -> {
          return new TransactionResponse(false, this.getAccountName(), this.getSequenceNumber());
        });
    if (r.isOk()) {
      oa.get().defer(this);
    }
    return Optional.of(r);
  }

  @Override
  public void commit(Optional<Account> account) {
    account.ifPresent(a -> a.setBalance(a.getBalance().add(BigInteger.valueOf(this.getDeposit()))));
    System.out.println(this.toString());
    System.out.flush();
  }
}
