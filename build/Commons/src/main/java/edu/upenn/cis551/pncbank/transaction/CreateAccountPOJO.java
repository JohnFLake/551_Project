package edu.upenn.cis551.pncbank.transaction;

import edu.upenn.cis551.pncbank.utils.PrintUtils;

public class CreateAccountPOJO extends AbstractTransaction {
  long balance;
  String validator;

  /**
   * Private constructor for use with Jackson.
   */
  private CreateAccountPOJO() {
    super(null, 0);
  }

  public CreateAccountPOJO(String accountName, String validator, long balance,
      long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.balance = balance;
    this.validator = validator;
  }

  public long getBalance() {
    return this.balance;
  }

  public String getValidator() {
    return this.validator;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    sb.append("\"account\":\"").append(this.getAccountName()).append("\",");
    sb.append("\"initial_balance\":").append(PrintUtils.writeCurrency(this.getBalance()));
    sb.append('}');
    return sb.toString();
  }

}
