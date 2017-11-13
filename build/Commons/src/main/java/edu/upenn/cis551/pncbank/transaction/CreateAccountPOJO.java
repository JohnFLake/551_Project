package edu.upenn.cis551.pncbank.transaction;

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

}
