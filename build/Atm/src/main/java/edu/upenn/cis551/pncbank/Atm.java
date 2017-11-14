package edu.upenn.cis551.pncbank;

import java.io.File;
import java.io.IOException;
import javax.crypto.SecretKey;
import org.apache.commons.cli.CommandLine;
import edu.upenn.cis551.pncbank.exception.NoRequestException;

public class Atm {


  private CommandLine cmd;
  private String ip;
  private String accountName;
  private String cardString;
  private SecretKey key;
  private int port;


  public Atm(CommandLine c, String i, int p, String ca, String n, SecretKey k) {
    this.accountName = n;
    this.cmd = c;
    this.cardString = ca;
    this.ip = i;
    this.key = k;
    this.port = p;
  }

  public boolean runCommand() throws IOException, NoRequestException {
    long amount = 0;

    Session session;

    // MAKE ACCOUNT:
    if (cmd.hasOption("n")) {

      // Validate account value:
      String accValue = cmd.getOptionValue("n");
      if (!InputValidator.isValidMoneyAmount(accValue)) {
        System.exit(255);
      } else {
        amount = InputValidator.convertDollarsToCents(accValue);
      }

      // Card file cannot exist.
      File f = new File(cardString);
      if (f.exists()) {
        System.exit(255);
      }

      // Create card:
      session = new Session(this.ip, this.port, this.key, cardString);
      Client.newAccount(session, accountName, amount);
      return true; // If the jvm hasn't exited, the request succeeded.

      // DEPOSIT:
    } else if (cmd.hasOption("d")) {

      String depValue = cmd.getOptionValue("d");
      if (!InputValidator.isValidMoneyAmount(depValue)) {
        System.exit(255);
      } else {
        amount = InputValidator.convertDollarsToCents(depValue);
      }

      // Get cardFile
      session = new Session(this.ip, this.port, this.key, cardString);
      return Client.Deposit(session, accountName, amount);
    }

    // WITHDRAW
    else if (cmd.hasOption("w")) {

      String wdValue = cmd.getOptionValue("w");
      if (!InputValidator.isValidMoneyAmount(wdValue)) {
        System.exit(255);
      } else {
        amount = InputValidator.convertDollarsToCents(wdValue);
      }

      // Get cardFile
      session = new Session(this.ip, this.port, this.key, cardString);
      return Client.Withdraw(session, accountName, amount);
    }

    // GET BALANCE:
    else if (cmd.hasOption("g")) {
      session = new Session(this.ip, this.port, this.key, cardString);
      return Client.checkBalance(session, accountName);
    }
    // No request.
    throw new NoRequestException();
  }

}


