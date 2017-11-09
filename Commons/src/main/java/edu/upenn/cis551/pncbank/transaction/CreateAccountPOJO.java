package edu.upenn.cis551.pncbank.transaction;

public class CreateAccountPOJO extends AbstractTransaction {
  final long balance;
  final String validator;

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
