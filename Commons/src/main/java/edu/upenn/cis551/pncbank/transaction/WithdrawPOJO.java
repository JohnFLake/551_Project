package edu.upenn.cis551.pncbank.transaction;

public class WithdrawPOJO extends AbstractTransaction {
  
  final long withdraw;

  public WithdrawPOJO(String accountName, long amount, long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.withdraw = amount;
  }

  /**
   * @return the amount
   */
  public final long getWithdraw() {
    return this.withdraw;
  }

}
