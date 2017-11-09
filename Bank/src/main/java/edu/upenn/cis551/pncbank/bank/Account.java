package edu.upenn.cis551.pncbank.bank;

import java.math.BigInteger;

/**
 * Representation of an account. Provides methods for adding or removing value from the account
 * subject to the validator.
 * 
 * @author jlautman
 *
 * @param <D> The type of the validator object that the account uses.
 */
public class Account {
  /**
   * Current account balance. Uses a BigInteger to prevent overflow/underflow.
   */
  private BigInteger balance;
  private String cardValidator;
  private long sequence;

  public Account(long balance, String validator, long sequence) {
    this.balance = BigInteger.valueOf(balance);
    this.cardValidator = validator;
    this.sequence = sequence;
  }

  /**
   * 
   * @param amount The change to make on the account value
   * @param validation The string to validate using the account's cardValidator.
   * @param sequence The sequence number that the request sent.
   * @return false iff the wrong sequence number is found, the validation fails, or the amount
   *         results in a negative balance.
   */
  public boolean updateValue(long amount, String validation, long sequence) {
    if (sequence != this.sequence) {
      // Wrong sequence number.
      return false;
    }
    // TODO use the validation with this account's card validator to ensure the transaction is
    // allowed.
    BigInteger newVal = this.balance.add(BigInteger.valueOf(amount));
    if (newVal.compareTo(BigInteger.ZERO) < 0) {
      return false;
    }
    this.balance = newVal;
    return true;
  }

  public long getSequence() {
    return this.sequence;
  }
}
