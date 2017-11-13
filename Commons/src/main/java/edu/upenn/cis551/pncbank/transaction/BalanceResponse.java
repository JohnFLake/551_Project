package edu.upenn.cis551.pncbank.transaction;

public class BalanceResponse extends TransactionResponse {
  /**
   * Reporting the balance as a strong to avoid breaking on arbitrarily large accounts.
   */
  String balance;

  /**
   * Private constructor for use with Jackson.
   */
  @SuppressWarnings("unused")
  private BalanceResponse() {
    super();
  }

  public BalanceResponse(boolean ok, String accountId, long sequence, String balance) {
    super(ok, accountId, sequence);
    this.balance = balance;
  }

  public String getBalance() {
    return this.balance;
  }
}
