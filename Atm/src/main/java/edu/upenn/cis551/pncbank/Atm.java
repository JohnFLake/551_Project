package edu.upenn.cis551.pncbank;

import java.io.File;
import java.math.BigDecimal;
import org.apache.commons.cli.CommandLine;
import edu.upenn.cis551.pncbank.encryption.Authentication;
import edu.upenn.cis551.pncbank.encryption.CardFile;

public class Atm {

  private static String accountName;
  private static String authFile;
  private static String IP;
  private static String cardFile;
  private static int port;

  public Atm(String s, String i, int p, String c, String a) {
    this.port = p;
    this.authFile = s;
    this.accountName = a;
    this.cardFile = c;
    this.IP = i;
  }



  /**
   * Simple helper method to check if a given string is a valid port number
   * 
   * @param str The port String we are checking
   * @return Whether this is a valid port number
   */
  public static boolean isValidPortNumber(String str) {
    if (str == null) {
      return false;
    }
    if (!str.matches("(0|[1-9][0-9]*)")) {
      return false;
    }
    try {
      int num = Integer.parseInt(str);
      if (num > 65535 || num < 1024) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean isValidAccountName(String str) {
    if (str == null) {
      return false;
    }
    if (str.length() < 1 || str.length() > 200) {
      return false;
    }
    if (!str.matches("[_\\-\\.0-9a-z]+")) {
      return false;
    }
    return true;
  }


  /**
   * Given an auth file, determine if it is a valid one or not.
   * 
   * @param str
   * @return
   */
  public static boolean isValidFile(String str) {
    if (str == null) {
      return false;
    }
    if (argTooLong(str)) {
      return false;
    }

    if (!str.matches("[_\\-\\.0-9a-z]+")) {
      return false;
    }

    if (str.matches("\\.")) {
      return false;
    }

    if (str.matches("\\.\\.")) {
      return false;
    }
    return true;
  }


  /**
   * Given a string representation of a number, determine if it is valid as an input.
   * 
   * @param str
   * @return
   */
  public static boolean isValidMoneyAmount(String str) {
    if (str == null) {
      return false;
    }
    if (argTooLong(str)) {
      return false;
    }
    if (!str.matches("(0|[1-9][0-9]*)(\\.[0-9]{2})?")) {
      return false;
    }

    try {
      BigDecimal b = new BigDecimal(str);
      BigDecimal upper = new BigDecimal("4294967295.99");
      BigDecimal lower = new BigDecimal("0.00");
      if (b.compareTo(lower) == -1 || b.compareTo(upper) == 1) {
        return false;
      }

    } catch (Exception e) {
      return false;
    }
    return true;
  }



  /**
   * Given a command line object with options, determine if the combination of arguments is valid.
   * 
   * @param cmd
   * @return
   */
  public static boolean properTransaction(CommandLine cmd) {

    // If no transaction is included, not valid.
    if (!cmd.hasOption("n") && !cmd.hasOption("d") && !cmd.hasOption("w") && !cmd.hasOption("g")) {
      return false;
    }

    // N cannot have d, w, or g as another option.
    if (cmd.hasOption("n") && (cmd.hasOption("d") || cmd.hasOption("w") || cmd.hasOption("g"))) {
      return false;
    }

    // D cannot have w or g. (n checked above).
    else if (cmd.hasOption("d") && (cmd.hasOption("w") || cmd.hasOption("g"))) {
      return false;
    }

    // W cannot have g. (d and n checked above).
    else if (cmd.hasOption("w") && cmd.hasOption("g")) {
      return false;
    }

    return true;
  }


  /**
   * Returns true if the given argument has over 4096 characters.
   * 
   * @param argument
   * @return
   */
  public static boolean argTooLong(String argument) {
    if (argument != null) {
      if (argument.length() > 4096)
        return true;
    }
    return false;
  }

  /**
   * Check if the given IP address is valid
   * 
   * @param str
   * @return
   */
  public static boolean isValidIP(String str) {

    final String ip_pattern =
        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    if (str == null) {
      return false;
    }
    if (argTooLong(str)) {
      return false;
    }
    if (!str.matches(ip_pattern)) {
      return false;
    }
    return true;
  }


  public static void cardFile(CommandLine cmd) {
    if (cmd.hasOption("a")) {
      if (!(new File(cmd.getOptionValue("a") + ".card")).exists()) {
        try {
          Authentication.saveCardFile(cmd.getOptionValue("a") + ".card",
              new CardFile(cmd.getOptionValue("a")));
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    else {
      System.exit(255);
    }
  }
}


