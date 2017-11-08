package edu.upenn.cis551.pncbank.transaction;

<<<<<<< HEAD
public class CreateAccountPOJO extends AbstractTransaction {
  final long balance;

  public CreateAccountPOJO(String accountName, long balance, long sequenceNumber) {
    super(accountName, sequenceNumber);
=======
public class CreateAccountPOJO {
  final String accountName;
  final long balance;

  public CreateAccountPOJO(String accountName, long balance) {
    this.accountName = accountName;
>>>>>>> Include the inital balance in the CreateAccountPOJO
    this.balance = balance;
  }

  public long getBalance() {
    return this.balance;
  }

  public long getBalance() {
    return this.balance;
  }
}
