package edu.upenn.cis551.pncbank.transaction;

public class DepositPOJO {
  final String accountName;
  final long amount;
  
  public DepositPOJO (String accountName, long amount) {
    this.accountName = accountName;
    this.amount = amount;
  }
}
