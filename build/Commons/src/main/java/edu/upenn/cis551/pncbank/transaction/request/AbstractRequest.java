package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import edu.upenn.cis551.pncbank.bank.IAccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

@JsonTypeInfo(use = Id.CLASS, //
    include = JsonTypeInfo.As.PROPERTY, //
    property = "type")
@JsonSubTypes({//
    @Type(value = CreateAccountRequest.class), //
    @Type(value = WithdrawRequest.class), //
    @Type(value = DepositRequest.class), //
    @Type(value = BalanceRequest.class), //
    @Type(value = AckRequest.class) //
})
public abstract class AbstractRequest {
  static final long maxCurrencyValue = 429496729599l;
  long sequenceNumber;
  String accountName;

  public AbstractRequest(String accountName, long sequenceNumber) {
    this.accountName = accountName;
    this.sequenceNumber = sequenceNumber;
  }

  public final long getSequenceNumber() {
    return this.sequenceNumber;
  }

  public final String getAccountName() {
    return this.accountName;
  }

  /**
   * Applies this transaction to an account.
   * 
   * @param accountName The name of the account.
   * @param am The account manager to apply to.
   * @return An optional transaction response (some transactions do not respond).
   */
  public abstract Optional<TransactionResponse> apply(String accountName, IAccountManager am);
}
