package edu.upenn.cis551.pncbank.transaction.request;

import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

/**
 * A request that sends an acknowledgement from the atm to the bank.
 * 
 * @author jlautman
 *
 */
public class AckRequest extends AbstractRequest {

  private AbstractRequest request;

  public AckRequest(AbstractRequest request) {
    super(request.getAccountName(), request.getSequenceNumber());
    this.request = request;
  }

  public AbstractRequest getRequest() {
    return this.request;
  }

  @Override
  public Optional<TransactionResponse> apply(AccountManager am) {
    am.commitAccount(this.getAccountName());
    am.get(accountName, this.sequenceNumber).ifPresent(a -> a.commit(this.getRequest()));
    return Optional.empty();
  }

  @Override
  public void commit(Optional<Account> account) {}

}
