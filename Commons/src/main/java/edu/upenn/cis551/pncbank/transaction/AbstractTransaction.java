package edu.upenn.cis551.pncbank.transaction;

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
