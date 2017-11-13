// Package for parsing command line arguments

package edu.upenn.cis551.pncbank;

import javax.crypto.SecretKey;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import edu.upenn.cis551.pncbank.encryption.Authentication;

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

      // Checks for proper ATM input
      if (!Atm.properTransaction(cmd)) {
        System.exit(255);
      }

      Atm.lengthChecker(cmd.getOptionValue("a"));
      if (!cmd.hasOption("a")) {
        System.exit(255);
      } else {
        accountName = cmd.getOptionValue("a");
      }

      Atm.lengthChecker(cmd.getOptionValue("c"));
      // Check for account name and update card file name


      // Check for IP address
      if (cmd.hasOption("i")) {
        Atm.lengthChecker(cmd.getOptionValue("i"));
        IP = cmd.getOptionValue("i");
      }

      // Check for port option
      if (cmd.hasOption("p")) {
        Atm.lengthChecker(cmd.getOptionValue("p"));
        String portString = cmd.getOptionValue("p");
        if (Atm.isValidPortNumber(portString)) {
          port = Integer.parseInt(portString);
        } else {
          System.out.println("Invalid port number.");
          System.exit(255);
        }
      }



      // Check for auth file
      if (cmd.hasOption("s")) {
        authFile = cmd.getOptionValue("s");
      }

      // Get auth key:
      authKey = Authentication.getAESKeyFromAuthFile(authFile);

      atmSession session = new atmSession(IP, port, authKey);


      // Check for transaction option and execute
      if (cmd.hasOption("n")) {

        Atm.lengthChecker(cmd.getOptionValue("n"));
        int amount = 0;
        try {

          System.out.println(cmd.getOptionValue("n"));
          amount = Integer.parseInt(cmd.getOptionValue("n"));


        } catch (NumberFormatException ex) {
          System.exit(255);
        }

        atmclient.newAccount(session, accountName, amount);
      }

      else if (cmd.hasOption("d")) {
        Atm.lengthChecker(cmd.getOptionValue("d"));
        try {
          Integer.parseInt(cmd.getOptionValue("n"));
        } catch (NumberFormatException ex) {
          System.exit(255);
        }

        atmclient.Deposit(session, accountName, Integer.parseInt(cmd.getOptionValue("d")));
      }

      else if (cmd.hasOption("w")) {
        Atm.lengthChecker(cmd.getOptionValue("w"));
        try {
          Integer.parseInt(cmd.getOptionValue("n"));
        } catch (NumberFormatException ex) {
          System.exit(255);
        }

        atmclient.Withdraw(session, accountName, Integer.parseInt(cmd.getOptionValue("w")));
      }

      else if (cmd.hasOption("g")) {
        Atm.lengthChecker(cmd.getOptionValue("g"));
        atmclient.checkBalance(session, accountName);
      }


    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("wrong args fam.");
    }

  }
}
