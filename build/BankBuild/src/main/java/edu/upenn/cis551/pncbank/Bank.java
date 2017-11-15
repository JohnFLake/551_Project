package edu.upenn.cis551.pncbank;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.crypto.SecretKey;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.encryption.AESEncryption;
import edu.upenn.cis551.pncbank.encryption.Authentication;
import edu.upenn.cis551.pncbank.encryption.EncryptionException;
import edu.upenn.cis551.pncbank.encryption.IEncryption;
import edu.upenn.cis551.pncbank.transaction.AbstractTransaction;
import edu.upenn.cis551.pncbank.transaction.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;

public class Bank implements AutoCloseable {

  public static final String DEFAULT_BANK_AUTH = "bank.auth";
  public static final String DEFAULT_BANK_PORT = "3000";
  static IEncryption<SecretKey, SecretKey> encryption = new AESEncryption();

  private final int port;
  private final SecretKey bankKey;
  private final AccountManager am;
  private final ObjectMapper mapper;
  private boolean notShutdown = true;

  /**
   * Construct a bank
   * 
   * @param port The port on which to listen
   * @param bankKey The bank key to use for all communcation
   * @param am The Account Manager, including all bank state
   */
  Bank(int port, SecretKey bankKey, AccountManager am) {
    this.port = port;
    this.bankKey = bankKey;
    this.am = am;
    this.mapper = new ObjectMapper();
    this.notShutdown = true;
  }

  void start() throws IOException {
    while (true) {
      try (ServerSocket sSocket = new ServerSocket(this.port)) {
        while (this.notShutdown) {
          Socket s = sSocket.accept();
          s.setSoTimeout(10000);
          handleTransaction(s);
        }
        break;
      }
    }
  }


  byte[] getBytesFromInputStream(InputStream in) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    byte[] buf = new byte[4096];
    while (true) {
      int n = in.read(buf);
      if (n <= 0)
        break;
      baos.write(buf, 0, n);
    }

    byte data[] = baos.toByteArray();
    return data;
  }


  void handleTransaction(Socket s) {
    try (InputStream in = s.getInputStream(); OutputStream out = s.getOutputStream();) {

      // Read encrypted input
      byte[] inputData = getBytesFromInputStream(in);

      // Decrypt into a transaction request
      byte[] decrypted = encryption.decrypt(inputData, this.bankKey);

      AbstractTransaction t = this.mapper.readValue(decrypted, AbstractTransaction.class);
      TransactionResponse tr = am.apply(t);
      printTransactionResults(t, tr);
      byte[] toSend = encryption.encrypt(this.mapper.writeValueAsBytes(tr), this.bankKey);
      out.write(toSend);
    } catch (EncryptionException | IOException e) {
      // Also catches SocketTimeoutExceptions due to read timeouts.
      System.out.println("protocol_error");
      System.out.flush();
    }
  }

  static void printTransactionResults(AbstractTransaction t, TransactionResponse r) {
    if (r.isOk()) {
      if (r instanceof BalanceResponse) {
        // special case for balance
        System.out.println(r.toString());
      } else {
        System.out.println(t.toString());
      }
    } else {
      System.out.println("protocol_error");
    }
    System.out.flush();
  }

  public static void main(String[] args) {
    System.setProperty("line.separator", "\n");
    String authFileName;
    int bankPort;
    SecretKey bankKey;

    // Get necessary info to start a bank.
    try {
      CommandLineParser clp = new DefaultParser();
      Options o = new Options();
      o.addOption("p", true, "The port for this server to run on");
      o.addOption("s", true, "The name of the auth file");
      CommandLine cl = clp.parse(o, args);
      authFileName = cl.getOptionValue("s", DEFAULT_BANK_AUTH);
      bankPort = Integer.parseInt(cl.getOptionValue("p", DEFAULT_BANK_PORT), 10);
      bankKey = Authentication.generateAuthFile(authFileName);
      System.out.println("created");
      System.out.flush();
    } catch (ParseException | NumberFormatException | EncryptionException | IOException e) {
      // Failed to parse args or to generate the authfile.
      System.exit(255);
      return;
    }

    // Set up the AtmService
    AccountManager am = new AccountManager();

    try (Bank bank = new Bank(bankPort, bankKey, am)) {
      bank.start();
    } catch (IOException e1) {
      // Failed to bind to the specified port
      System.exit(255);
      return;
    }
  }

  @Override
  public void close() throws IOException {
    this.notShutdown = false;
  }

}
