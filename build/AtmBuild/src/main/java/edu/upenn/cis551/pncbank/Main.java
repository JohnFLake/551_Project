// Package for parsing command line arguments

package edu.upenn.cis551.pncbank;

import java.io.IOException;
import java.util.HashSet;
import javax.crypto.SecretKey;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import edu.upenn.cis551.pncbank.encryption.Authentication;
import edu.upenn.cis551.pncbank.exception.NoRequestException;
import edu.upenn.cis551.pncbank.utils.InputValidator;

public class Main {


  private static int port;
  private static String IP;
  private static String authFile;
  private static String accountName;
  private static String cardFile;
  private static SecretKey authKey;


  public static void setupOptions(Options options) {
    options.addOption("a", true, "The customer's account name");
    options.addOption("s", true, "The authorization file");
    options.addOption("i", true, "The IP address for this server to run on");
    options.addOption("p", true, "The port for this server to run on");
    options.addOption("c", true, "The customer's ATM card file");
    options.addOption("n", true, "Create a new account");
    options.addOption("d", true, "Deposit money");
    options.addOption("w", true, "Withdrawl Money");
    options.addOption("g", false, "Check Balance");
  }

  public static void setDefaults() {
    port = 3000;
    IP = "127.0.0.1";
    authFile = "bank.auth";
    accountName = "";
    cardFile = "";
    authKey = null;
  }

  public static CommandLine parseOptions(Options options, String[] args)
      throws ParseException, IOException {


    CommandLineParser parser = new DefaultParser();
    // Attempt to parse the options:

    boolean sH = false;
    boolean cH = false;
    boolean aH = false;
    // For any argument that accepts a string, make sure that the string can start with a hyphen
    for (int i = 0; i < args.length - 1; i++) {
      String arg = args[i];
      if (arg.equals("-s") || arg.equals("-gs")) {
        sH = true;
        StringBuilder argChanger = new StringBuilder();
        argChanger.append("_");
        argChanger.append(args[i + 1]);
        args[i + 1] = argChanger.toString();
      } else if (arg.equals("-a") || arg.equals("-ga")) {
        aH = true;
        StringBuilder argChanger = new StringBuilder();
        argChanger.append("_");
        argChanger.append(args[i + 1]);
        args[i + 1] = argChanger.toString();
      } else if (arg.equals("-c") || arg.equals("-gc")) {
        cH = true;
        StringBuilder argChanger = new StringBuilder();
        argChanger.append("_");
        argChanger.append(args[i + 1]);
        args[i + 1] = argChanger.toString();
      }


    }


    CommandLine cmd = parser.parse(options, args);
    // System.err.println("Parsed arguments.");


    // Remove the underscore we added.
    for (int i = 0; i < args.length - 1; i++) {
      String arg = args[i];
      if (arg.equals("-s")) {
        if (sH)
          args[i + 1] = args[i + 1].substring(1);
      } else if (arg.equals("-a")) {
        if (aH)
          args[i + 1] = args[i + 1].substring(1);
      } else if (arg.equals("-c")) {
        if (cH)
          args[i + 1] = args[i + 1].substring(1);
      }
    }



    // Checks if this is a proper transaction.
    if (!InputValidator.properTransaction(cmd)) {
      System.exit(255);
    }



    // ACCOUNTNAME:
    if (!cmd.hasOption("a")) {
      System.exit(255);
    } else {
      accountName = cmd.getOptionValue("a");
      if (aH)
        accountName = accountName.substring(1);
      if (!InputValidator.isValidAccountName(accountName)) {
        System.exit(255);
      }

      // Default card file
      cardFile = accountName + ".card";
      if (cH)
        cardFile = cardFile.substring(1);
    }


    // AUTHFILE AND KEY:
    authFile = cmd.getOptionValue("s", "bank.auth");
    if (sH)
      authFile = authFile.substring(1);
    if (!InputValidator.isValidFile(authFile)) {
      System.exit(255);
    }
    authKey = Authentication.getAESKeyFromAuthFile(authFile);

    // IP ADDRESS:
    if (cmd.hasOption("i")) {
      IP = cmd.getOptionValue("i");
      if (!InputValidator.isValidIP(IP)) {
        System.exit(255);
      }
    }

    // PORT:
    if (cmd.hasOption("p")) {
      String portString = cmd.getOptionValue("p");
      if (!InputValidator.isValidPortNumber(portString)) {
        System.exit(255);
      } else {
        port = Integer.parseInt(portString);
      }
    }


    // CARD FILE:
    if (cmd.hasOption("c")) {
      cardFile = cmd.getOptionValue("c");
      if (cH)
        cardFile = cardFile.substring(1);
      if (!InputValidator.isValidFile(cardFile)) {
        System.exit(255);
      }
    }
    return cmd;
  }

  public static void main(String[] args) {
    // Add options:
    Options options = new Options();
    setupOptions(options);

    // Default values for options:
    setDefaults();

    // Attempt to parse commands and start an atm transaction:
    try {
      CommandLine cmd = parseOptions(options, args);

      // Make sure each option is at most used once.
      HashSet<String> seenOptions = new HashSet<>();
      for (Option o : cmd.getOptions()) {
        if (seenOptions.contains(o.getOpt())) {
          System.exit(255);
        } else {
          seenOptions.add(o.getOpt());
        }
      }


      // Have the ATM perform the correct action.
      Atm a = new Atm(cmd, IP, port, cardFile, accountName, authKey);
      // Keep trying as long as the command returns false. May call System.exit to exit the jvm.
      while (!a.runCommand());

    } catch (ParseException | IOException | NoRequestException e) {
      System.exit(255);
    }

    System.exit(0);
  }
}
