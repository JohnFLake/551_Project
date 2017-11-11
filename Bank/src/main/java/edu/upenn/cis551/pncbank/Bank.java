package edu.upenn.cis551.pncbank;

import java.io.IOException;
import javax.crypto.SecretKey;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.bank.atmservice.AtmService;
import edu.upenn.cis551.pncbank.encryption.Authentication;
import edu.upenn.cis551.pncbank.encryption.EncryptionException;
import edu.upenn.cis551.pncbank.encryption.IEncryption;

public class Bank {

  public static final String DEFAULT_BANK_AUTH = "bank.auth";
  public static final String DEFAULT_BANK_PORT = "3000";
  static SecretKey bankKey;
  static IEncryption<SecretKey, SecretKey> encryption;


  public static void main(String[] args) {
    // Extract arguments
    Options o = new Options();
    o.addOption("p", true, "The port for this server to run on");
    o.addOption("s", true, "The name of the auth file");

    CommandLineParser clp = new DefaultParser();
    String authFileName;
    int bankPort;
    try {
      CommandLine cl = clp.parse(o, args);
      authFileName = cl.getOptionValue("s", DEFAULT_BANK_AUTH);
      bankPort = Integer.parseInt(cl.getOptionValue("p", DEFAULT_BANK_PORT), 10);
    } catch (ParseException | NumberFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(255);
      return;
    }
    

    try {
      bankKey = Authentication.generateAuthFile(authFileName);
    } catch (EncryptionException | IOException e1) {
      System.exit(255);
      return;
    }

    // Set up the AtmService
    AccountManager am = new AccountManager();
    AtmService service;
    try {
      service = new AtmService(bankPort, bankKey);
    } catch (IOException e) {
      // Failed to bind to the port
      System.exit(255);
      return;
    }
    // Main atm servicing loop
    while (true) {
      service.handleNextRequest(am);
    }
  }

}
