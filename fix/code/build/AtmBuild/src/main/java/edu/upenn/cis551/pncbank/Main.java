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

    Option accName = new Option("a", true, "The customer's account name");
    accName.setRequired(true);
    options.addOption(accName);
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



    for (int i = 0; i < args.length - 1; i++) {
      String arg = args[i];

      // When parsing options that accept strings, prepend an underscore to the string.
      if ((arg.equals("-s") || arg.equals("-a") || arg.equals("-c"))) {

        StringBuilder argChanger = new StringBuilder();
        argChanger.append("_");
        argChanger.append(args[i + 1]);
        args[i + 1] = argChanger.toString();

        // When parsing -g, the only acceptable next argument is another option.
      } else if (arg.equals("-g")) {
        if (!args[i + 1].equals("-s") && !args[i + 1].equals("-i") && !args[i + 1].equals("-p")
            && !args[i + 1].equals("-c") && !args[i + 1].equals("-n") && !args[i + 1].equals("-d")
            && !args[i + 1].equals("-w") && !args[i + 1].equals("-g"))
          System.exit(255);
      }
    }

    CommandLineParser parser = new DefaultParser();


    // Attempt to parse the options:
    /*
     * System.err.println("Parsing options."); for (String s : args) { System.err.println("Arg: " +
     * s); }
     */
    CommandLine cmd = parser.parse(options, args);



    /*
     * for (Option o : cmd.getOptions()) { System.err.println("Option: " + o.getOpt());
     * System.err.println("Value: " + o.getValue()); }
     */

    // Checks if this is a proper transaction.
    if (!InputValidator.properTransaction(cmd)) {
      System.err.println("Invalid transaction.");
      System.exit(255);
    }

    // ACCOUNTNAME:
    if (!cmd.hasOption("a")) {
      System.err.println("No accountname given.");
      System.exit(255);
    } else {
      accountName = cmd.getOptionValue("a");
      accountName = accountName.substring(1);

      if (!InputValidator.isValidAccountName(accountName)) {
        System.err.println("Invalid account name: " + accountName);
        System.exit(255);
      }

      // Default card file
      cardFile = accountName + ".card";
      cardFile = cardFile.substring(1);

    }


    // AUTHFILE AND KEY:
    authFile = cmd.getOptionValue("s", "bank.auth");
    authFile = authFile.substring(1);
    if (!InputValidator.isValidFile(authFile)) {
      System.exit(255);
    }
    System.err.println("Getting key.");



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
      if (!InputValidator.isValidFile(cardFile)) {
        System.exit(255);
      }
    }
    System.err.println("Parsing complete.");
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
      System.err.println("accountName: " + accountName);
      Atm a = new Atm(cmd, IP, port, cardFile, accountName, authKey);
      // Keep trying as long as the command returns false. May call System.exit to exit the jvm.
      while (!a.runCommand());

    } catch (ParseException | IOException | NoRequestException e) {
      System.err.println("Error parsing.");
      System.err.println(e.getMessage());
      System.exit(255);
    }

    System.exit(0);
  }
}
