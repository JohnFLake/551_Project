package edu.upenn.cis551.pncbank.transaction;

public class BalancePOJO extends AbstractTransaction {
  final String validation;

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
