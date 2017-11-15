package edu.upenn.cis551.pncbank.transaction.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import edu.upenn.cis551.pncbank.bank.Account;
import edu.upenn.cis551.pncbank.bank.IAccountManager;
import edu.upenn.cis551.pncbank.transaction.response.TransactionResponse;

/**
 * Simplified account manager for use with tests
 * 
 * @author jlautman
 *
 */
public class TAcctMgr implements IAccountManager {

  Map<String, Account> accounts = new HashMap<>();

  @Override
  public Optional<TransactionResponse> apply(AbstractRequest t) {
    return t.apply(this);
  }

  @Override
  public Optional<Account> get(String accountName) {
    // TODO Auto-generated method stub
    return Optional.ofNullable(accounts.get(accountName));
  }

  @Override
  public Optional<Account> createAccount(String accountName, String validator, long sequenceNumber,
      long balance) {
    Account a = new Account(validator, sequenceNumber);
    a.updateValueAndIncrementSeq(balance);
    accounts.put(accountName, a);
    return Optional.of(a);
  }

}
