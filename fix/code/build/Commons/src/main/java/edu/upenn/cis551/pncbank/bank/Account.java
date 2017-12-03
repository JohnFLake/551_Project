package edu.upenn.cis551.pncbank.bank;

import java.math.BigInteger;
import java.util.Optional;
import edu.upenn.cis551.pncbank.transaction.request.AbstractRequest;
import edu.upenn.cis551.pncbank.transaction.request.BalanceRequest;
import edu.upenn.cis551.pncbank.utils.PrintUtils;

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
  BigInteger balance;
  String cardValidator;
  long sequence;

  AbstractRequest pending = null;

  public Account(String validator, long sequence) {
    this.balance = BigInteger.ZERO;
    this.cardValidator = validator;
    this.sequence = sequence;
  }

  /**
   * @return the balance
   */
  public final BigInteger getBalance() {
    return this.balance;
  }

  public final void setBalance(BigInteger i) {
    this.balance = i;
  }

  /**
   * @return the cardValidator
   */
  public final String getCardValidator() {
    return this.cardValidator;
  }

  /**
   * @return the sequence
   */
  public final long getSequence() {
    return this.sequence;
  }


  /**
   * Commits the pending transaction with the correct sequence number
   * 
   * @param s The sequence number of the transaction to commit.
   */
  public void commit(AbstractRequest transaction) {
    if (this.pending != null && this.pending.equals(transaction)) {
      this.pending.commit(Optional.of(this));
      if (this.pending instanceof BalanceRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"account\": \"").append(transaction.getAccountName()).append("\", ");
        sb.append("\"balance\": ").append(PrintUtils.writeCurrency(this.getBalance().toString()));
        sb.append('}');
        System.out.println(sb.toString());
      } else {
        System.out.println(this.pending.toString());
      }
      System.out.flush();
      this.sequence++;
      this.pending = null;
    }
  }

  public void defer(AbstractRequest r) {
    this.pending = r;
  }

  public void incrementSequence() {
    this.sequence++;
  }
}
