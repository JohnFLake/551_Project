package edu.upenn.cis551.pncbank.transaction;

public class CreateAccountPOJO extends AbstractTransaction {
  final long balance;

  public CreateAccountPOJO(String accountName, long balance, long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.balance = balance;
  }

  public long getBalance() {
    return this.balance;
  }
}
