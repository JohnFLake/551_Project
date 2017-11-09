package edu.upenn.cis551.pncbank.transaction;

public class TransactionResponse {
  final boolean ok;
  final String accountId;
  final long sequence;

  public TransactionResponse(boolean ok, String accountId, long sequence) {
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
