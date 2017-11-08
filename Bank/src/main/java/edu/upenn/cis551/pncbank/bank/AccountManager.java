package edu.upenn.cis551.pncbank.bank;

import java.util.HashMap;
import java.util.Map;

public class AccountManager<D> {
  /**
   * Singleton instance of the account manager.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  static final AccountManager INSTANCE = new AccountManager(new HashMap<String, Account>());

  final Map<String, Account<D>> accounts;

  private AccountManager(Map<String, Account<D>> accounts) {
    this.accounts = accounts;
  }
  
  public boolean deposit(String accountId, String validation, long value) {
    Account<D> account = this.accounts.get(accountId);
    if(null == account) return false;
    return account.updateValue(value, validation);
  }
}
