package edu.upenn.cis551.pncbank.transaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.CLASS, //
    include = JsonTypeInfo.As.PROPERTY, //
    property = "type")
@JsonSubTypes({//
    @Type(value = CreateAccountPOJO.class), //
    @Type(value = WithdrawPOJO.class), //
    @Type(value = DepositPOJO.class), //
    @Type(value = BalancePOJO.class)//
})
public abstract class AbstractTransaction {
  long sequenceNumber;
  String accountName;

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
