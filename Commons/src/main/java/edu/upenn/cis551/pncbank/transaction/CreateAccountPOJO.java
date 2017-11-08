package edu.upenn.cis551.pncbank.transaction;

public class CreateAccountPOJO {
  final String accountName;


  public CreateAccountPOJO(String accountName) {
    this.accountName = accountName;
  }

  public String getAccountName() {
    return this.accountName;
  }
}
