package edu.upenn.cis551.pncbank.transaction;

public class DepositPOJO extends AbstractTransaction {
  final long deposit;

  public DepositPOJO(String accountName, long amount, long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.deposit = amount;
  }

  public final long getDeposit() {
    return this.deposit;
  }
}
