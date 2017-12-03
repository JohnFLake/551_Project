package edu.upenn.cis551.pncbank.transaction.request;

import java.math.BigInteger;
import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.bank.AccountManager;
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
  public Optional<TransactionResponse> apply(AccountManager am) {
    Optional<Account> account = am.get(this.getAccountName(), this.getSequenceNumber());
    TransactionResponse r = account.filter(a -> a.getCardValidator().equals(this.getValidation()))
        .filter(a -> a.getSequence() == this.getSequenceNumber())
        .filter(a -> this.getWithdraw() > 0).filter(a -> a.getBalance()
            .subtract(BigInteger.valueOf(this.getWithdraw())).compareTo(BigInteger.ZERO) >= 0)
        .map(a -> {
          return new TransactionResponse(true, this.getAccountName(), this.getSequenceNumber());
        }).orElseGet(() -> {
          return new TransactionResponse(false, this.getAccountName(), this.getSequenceNumber());
        });

    if (r.isOk()) {
      account.get().defer(this);
    }
    return Optional.of(r);
  }

  @Override
  public void commit(Optional<Account> account) {
    account.ifPresent(
        a -> a.setBalance(a.getBalance().subtract(BigInteger.valueOf(this.getWithdraw()))));
  }


  @Override
  public boolean equals(Object other) {
    if (!(other instanceof WithdrawRequest))
      return false;
    WithdrawRequest r = (WithdrawRequest) other;
    return Optional.ofNullable(r.getAccountName())
        .filter(name -> name.equals(this.getAccountName())).isPresent()
        && r.getSequenceNumber() == this.getSequenceNumber()
        && Optional.ofNullable(r.getValidation()).filter(val -> val.equals(this.getValidation()))
            .isPresent()
        && this.getWithdraw() == r.getWithdraw();
  }
}
