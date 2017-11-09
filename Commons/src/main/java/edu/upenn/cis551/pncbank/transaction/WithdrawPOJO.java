package edu.upenn.cis551.pncbank.transaction;

public class WithdrawPOJO extends AbstractTransaction {

  final long withdraw;
  final String validation;

  public WithdrawPOJO(String accountName, String validation, long amount, long sequenceNumber) {
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

}
