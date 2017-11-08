package edu.upenn.cis551.pncbank.transaction;

public class CreateAccountPOJO {
  final String accountName;
  final long balance;

  public CreateAccountPOJO(String accountName, long balance) {
    this.accountName = accountName;
    this.balance = balance;
  }

  public String getAccountName() {
    return this.accountName;
  }

  public long getBalance() {
    return this.balance;
  }
}
