// Package for parsing command line arguments

package edu.upenn.cis551.pncbank;

import java.math.BigDecimal;
import javax.crypto.SecretKey;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import edu.upenn.cis551.pncbank.encryption.Authentication;
import edu.upenn.cis551.pncbank.encryption.EncryptionException;

public class Main {

  public static void main(String[] args) {

    // Add options:
    Options options = new Options();
    options.addOption("a", true, "The customer's account name");
    options.addOption("s", true, "The authorization file");
    options.addOption("i", true, "The IP address for this server to run on");
    options.addOption("p", true, "The port for this server to run on");
    options.addOption("c", true, "The customer's ATM card file");
    options.addOption("n", true, "Create a new account");
    options.addOption("d", true, "Deposit money");
    options.addOption("w", true, "Withdrawl Money");
    options.addOption("g", false, "Check Balance");
    CommandLineParser parser = new DefaultParser();

    int port = 3000;
    String IP = "127.0.0.1";
    String authFile = "bank.auth";
    String accountName = "";
    String cardFile = "";
    String sendstring = null;
    SecretKey authKey = null;

    try {
      CommandLine cmd = parser.parse(options, args);

      // Checks if this is a proper transaction.
      if (!Atm.properTransaction(cmd)) {
        System.exit(255);
      }

      // Check if we have the required parameter a:
      if (!cmd.hasOption("a")) {
        System.exit(255);
      } else {
        accountName = cmd.getOptionValue("a");
        if (!Atm.isValidAccountName(accountName)) {
          System.exit(255);
        }

        // Default card file
        cardFile = accountName + ".card";
      }


      // Check for optional parameter s:
      authFile = cmd.getOptionValue("s", "bank.auth");
      if (!Atm.isValidFile(authFile)) {
        System.exit(255);
      }

      // Get the Secret Key from the file:
      try {
        authKey = Authentication.getAESKeyFromAuthFile(authFile);
      } catch (EncryptionException e) {
        System.exit(255);
      }

      // Check for optional parameter i:
      if (cmd.hasOption("i")) {
        IP = cmd.getOptionValue("i");
        if (!Atm.isValidIP(IP)) {
          System.exit(255);
        }
      }

      // Check for optional parameter p:
      if (cmd.hasOption("p")) {
        String portString = cmd.getOptionValue("p");
        if (!Atm.isValidPortNumber(portString)) {
          System.exit(255);
        } else {
          port = Integer.parseInt(portString);
        }
      }


      // Check for optional parameter c:
      if (cmd.hasOption("c")) {
        cardFile = cmd.getOptionValue("c");
        if (!Atm.isValidFile(cardFile)) {
          System.exit(255);
        }
      }


      atmSession session = new atmSession(IP, port, authKey);
      BigDecimal amount = null;

      // MAKE ACCOUNT:
      if (cmd.hasOption("n")) {
        String accValue = cmd.getOptionValue("n");
        if (!Atm.isValidMoneyAmount(accValue)) {
          System.exit(255);
        } else {
          amount = new BigDecimal(accValue);
        }
        atmclient.newAccount(session, accountName, amount.intValue());


        // DEPOSIT:
      } else if (cmd.hasOption("d")) {

        String depValue = cmd.getOptionValue("d");
        if (!Atm.isValidMoneyAmount(depValue)) {
          System.exit(255);
        } else {
          amount = new BigDecimal(depValue);
        }
        atmclient.Deposit(session, accountName, amount.intValue());
      }

      // WITHDRAW
      else if (cmd.hasOption("w")) {

        String wdValue = cmd.getOptionValue("d");
        if (!Atm.isValidMoneyAmount(wdValue)) {
          System.exit(255);
        } else {
          amount = new BigDecimal(wdValue);
        }
        atmclient.Withdraw(session, accountName, amount.intValue());
      }

      // GET BALANCE:
      else if (cmd.hasOption("g")) {
        atmclient.checkBalance(session, accountName);
      }


    } catch (Exception e) {
      System.exit(255);
    }

  }
}
