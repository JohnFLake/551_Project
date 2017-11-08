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
public class Account<D> {
  /**
   * The id of this account.
   */
  private final String accountId;
  /**
   * Current account balance. Uses a BigInteger to prevent overflow/underflow.
   */
  private BigInteger balance;
  private D cardValidator;

  public Account(String id, long balance, D validator) {
    this.accountId = id;
    this.balance = BigInteger.valueOf(balance);
    this.cardValidator = validator;
  }

  public boolean updateValue(long amount, String validation) {
    // TODO use the validation with this account's card validator to ensure the transaction is
    // allowed.
    BigInteger newVal = this.balance.add(BigInteger.valueOf(amount));
    if (newVal.compareTo(BigInteger.ZERO) < 0) {
      return false;
    }
    this.balance = newVal;
    return true;
  }
}
