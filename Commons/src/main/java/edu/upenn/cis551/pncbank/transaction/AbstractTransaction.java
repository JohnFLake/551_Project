package edu.upenn.cis551.pncbank.transaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({//
    @JsonSubTypes.Type(value = CreateAccountPOJO.class, name = "CreateAccount"),
    @JsonSubTypes.Type(value = WithdrawPOJO.class, name = "Withdraw"),
    @JsonSubTypes.Type(value = DepositPOJO.class, name = "Deposit")//
})
public class AbstractTransaction {
  final long sequenceNumber;
  final String accountName;

  public AbstractTransaction(String accountName, long sequenceNumber) {
    this.accountName = accountName;
    this.sequenceNumber = sequenceNumber;
  }

  public final long getSequenceNumber() {
    return this.sequenceNumber;
  }

  public final String getAccountName() {
    return this.accountName;
  }
}
