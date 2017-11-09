package edu.upenn.cis551.pncbank.transaction;

public class DepositPOJO extends AbstractTransaction {
  final long deposit;
  final String validation;

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
}
