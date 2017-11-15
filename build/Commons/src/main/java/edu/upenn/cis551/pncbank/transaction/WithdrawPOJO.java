package edu.upenn.cis551.pncbank.transaction;

import edu.upenn.cis551.pncbank.utils.PrintUtils;

public class WithdrawPOJO extends AbstractTransaction {

  long withdraw;
  String validation;

  /**
   * Private constructor for use with Jackson.
   */
  private WithdrawPOJO() {
    super(null, 0);
  }

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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    sb.append("\"account\":\"").append(this.getAccountName()).append("\",");
    sb.append("\"withdraw\":").append(PrintUtils.writeCurrency(this.getWithdraw()));
    sb.append('}');
    return sb.toString();
  }

}
