package edu.upenn.cis551.pncbank;

import java.io.File;
import javax.crypto.SecretKey;
import org.apache.commons.cli.CommandLine;

public class Atm {


  private CommandLine cmd;
  private String ip;
  private String accountName;
  private String cardString;
  private SecretKey key;
  private int port;
  private Session session;


  public Atm(CommandLine c, String i, int p, String ca, String n, SecretKey k) {
    this.accountName = n;
    this.cmd = c;
    this.cardString = ca;
    this.ip = i;
    this.key = k;
    this.port = p;

  }



  public void runCommand() throws Exception {
    long amount = 0;

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
      Client.Deposit(session, accountName, amount);
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
      Client.Withdraw(session, accountName, amount);
    }

    // GET BALANCE:
    else if (cmd.hasOption("g")) {
      session = new Session(this.ip, this.port, this.key, cardString);
      Client.checkBalance(session, accountName);
    }
  }

}


