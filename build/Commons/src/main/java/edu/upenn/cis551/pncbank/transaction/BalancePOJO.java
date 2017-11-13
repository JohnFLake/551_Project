package edu.upenn.cis551.pncbank.transaction;

public class BalancePOJO extends AbstractTransaction {
  String validation;

  /**
   * Private constructor for use with Jackson.
   */
  private BalancePOJO() {
    super(null, 0);
  }

  public BalancePOJO(String accountName, String validation, long sequenceNumber) {
    super(accountName, sequenceNumber);
    this.validation = validation;
  }

  /**
   * @return the validation
   */
  public final String getValidation() {
    return this.validation;
  }
}
