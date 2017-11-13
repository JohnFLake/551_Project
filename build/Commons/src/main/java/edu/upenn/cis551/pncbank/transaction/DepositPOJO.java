package edu.upenn.cis551.pncbank.transaction;

import edu.upenn.cis551.pncbank.utils.PrintUtils;

public class DepositPOJO extends AbstractTransaction {
  long deposit;
  String validation;

  /**
   * Private constructor for use with Jackson.
   */
  private DepositPOJO() {
    super(null, 0);
  }

  public DepositPOJO(String accountName, String validation, long amount, long sequenceNumber) {
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
}
