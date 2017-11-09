package edu.upenn.cis551.pncbank.bank;

import java.util.HashMap;
import java.util.Map;

public class AccountManager {
  Map<String, Account> accounts;

  public AccountManager() {
    this.accounts = new HashMap<String, Account>();
  }

  public synchronized boolean createAccount(String accountId, String validation, long val) {
    if (this.accounts.containsKey(accountId)) {
      return false;
    } else {
      this.accounts.put(accountId, new Account(val, validation));
      return true;
    }
  }

  public boolean changeValue(String accountId, String validation, long delta) {
    Account account = this.accounts.get(accountId);
    if (null == account) {
      return false;
    }
    return account.updateValue(delta, validation);
  }
}
