package edu.upenn.cis551.pncbank.transaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.CLASS, //
    include = JsonTypeInfo.As.PROPERTY, //
    property = "type")
@JsonSubTypes({//
    @Type(value = BalanceResponse.class) //

})
public class TransactionResponse {
  boolean ok;
  String accountId;
  long sequence;

  /**
   * Private constructor for use with Jackson.
   */
  TransactionResponse() {}

  public TransactionResponse(boolean ok, String accountId, long sequence) {
    this();
    this.ok = ok;
    this.accountId = accountId;
    this.sequence = sequence;
  }

  /**
   * @return if the transaction was ok
   */
  public final boolean isOk() {
    return this.ok;
  }

  /**
   * @return the accountId
   */
  public final String getAccountId() {
    return this.accountId;
  }

  /**
   * @return the sequence
   */
  public final long getSequence() {
    return this.sequence;
  }
}
