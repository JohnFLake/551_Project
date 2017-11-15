package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.IAccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

/**
 * A request that sends an acknowledgement from the atm to the bank.
 * 
 * @author jlautman
 *
 */
public class AckRequest extends AbstractRequest {

  // Needs no additional information
  public AckRequest(String accountName, long sequenceNumber) {
    super(accountName, sequenceNumber);
  }

  @Override
  public Optional<TransactionResponse> apply(IAccountManager am) {
    am.get(this.getAccountName()).ifPresent(account -> account.commit(this.getSequenceNumber()));
    return Optional.empty();
  }

}