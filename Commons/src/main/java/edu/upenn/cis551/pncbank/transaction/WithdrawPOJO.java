package edu.upenn.cis551.pncbank.transaction;

public class WithdrawPOJO {
  final String accountName;
  final long amount;

  public WithdrawPOJO(String accountName, long amount) {
    this.accountName = accountName;
    this.amount = amount;
  }

  /**
   * @return the accountName
   */
  public final String getAccountName() {
    return this.accountName;
  }

  /**
   * @return the amount
   */
  public final long getAmount() {
    return this.amount;
  }

}
