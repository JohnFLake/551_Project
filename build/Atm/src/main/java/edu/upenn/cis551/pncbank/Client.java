package edu.upenn.cis551.pncbank;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import javax.crypto.SecretKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upenn.cis551.pncbank.encryption.AESEncryption;
import edu.upenn.cis551.pncbank.encryption.Authentication;
import edu.upenn.cis551.pncbank.encryption.CardFile;
import edu.upenn.cis551.pncbank.encryption.EncryptionException;
import edu.upenn.cis551.pncbank.encryption.IEncryption;
import edu.upenn.cis551.pncbank.transaction.AbstractTransaction;
import edu.upenn.cis551.pncbank.transaction.BalancePOJO;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.DepositPOJO;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;
import edu.upenn.cis551.pncbank.transaction.WithdrawPOJO;

public class Client {
  static IEncryption<SecretKey, SecretKey> encryption = new AESEncryption();

  public static TransactionResponse sendPOJO(AbstractTransaction pojo, Session session) {

    ObjectMapper objectMapper = new ObjectMapper();
    Socket AtmBank = null;

    // Encrypt this POJO:
    SecretKey encryptKey = session.getsecretkey();
    byte[] encrypted = null;
    try {
      encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(pojo), encryptKey);
    } catch (JsonProcessingException | EncryptionException e1) {
      System.exit(255);
    }

    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      AtmBank.setSoTimeout(10 * 1000);

      // Send the encrypted bytes and receive a response:
      byte[] response = Session.writeToAndReadFromSocket(AtmBank, encrypted);

      // The response is encrypted. we need to decrypt it.
      byte[] decryptedResponse = null;

      // Encryption error has it's own exit code
      try {
        decryptedResponse = encryption.decrypt(response, encryptKey);
      } catch (EncryptionException e) {
        System.exit(255);
      }

      TransactionResponse tResponse =
          objectMapper.readValue(decryptedResponse, TransactionResponse.class);
      if (tResponse == null) {
        System.exit(255);
      }
      return tResponse;

    } catch (IOException e) {
      // ERROR with connection, including timeout.
      System.err.println("Error with the socket: " + e.getMessage());
      System.exit(63);
    }
    return null;
  }


  /**
   * Update the seq number of a card file.
   * 
   * @param card
   * @param cardName
   * @param seq
   */
  public static void updateCardSeqNumber(CardFile card, String cardName, long seq) {
    card.setSequenceNumber(seq);
    try {
      Authentication.saveCardFile(cardName, card);
    } catch (Exception e) {
      System.exit(255);
    }
  }


  public static void newAccount(Session session, String accountName, long balance) {
    if (balance < 10) {
      System.exit(255);
    }

    // Make a new cardfile
    CardFile newCard = new CardFile(accountName);
    try {
      Authentication.saveCardFile(session.getCard(), newCard);
    } catch (Exception e1) {
      System.exit(255);
    }


    // Make pojo to send.
    CreateAccountPOJO pojo =
        new CreateAccountPOJO(accountName, newCard.getPin(), balance, newCard.getSequenceNumber());


    // Send pojo and get response. Print it.
    TransactionResponse tResponse = sendPOJO(pojo, session);
    handleResponse(pojo, tResponse, newCard, session.getCard());
    // Note, there's no way that the bank can suggest a retry for this type of request.
  }


  public static boolean Deposit(Session session, String accountName, long deposit)
      throws IOException {

    // Check on the proper formatting of this
    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    DepositPOJO pojo =
        new DepositPOJO(accountName, checkCard.getPin(), deposit, checkCard.getSequenceNumber());

    // Send pojo and get response. Print it.
    TransactionResponse tResponse = sendPOJO(pojo, session);
    return handleResponse(pojo, tResponse, checkCard, session.getCard());
  }

  public static boolean Withdraw(Session session, String accountName, long withdraw)
      throws IOException {
    if (withdraw < 0) {
      System.exit(255);
    }

    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    WithdrawPOJO pojo =
        new WithdrawPOJO(accountName, checkCard.getPin(), withdraw, checkCard.getSequenceNumber());

    // Send pojo and get response. Print it.
    TransactionResponse tResponse = sendPOJO(pojo, session);
    return handleResponse(pojo, tResponse, checkCard, session.getCard());

  }



  public static boolean checkBalance(Session session, String accountName) throws IOException {
    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    BalancePOJO pojo =
        new BalancePOJO(accountName, checkCard.getPin(), checkCard.getSequenceNumber());

    // Send pojo and get response. Print it.
    TransactionResponse tResponse = sendPOJO(pojo, session);
    return handleResponse(pojo, tResponse, checkCard, session.getCard());
  }

  /**
   * Helper function to handle the response from the bank. If <code>false</code> is returned the
   * request should be retried with the updated card file.<br/>
   * If the response indicates that the request failed and that a retry won't work, causes the JVM
   * to exit with code 255.
   * 
   * @param request
   * @param response
   * @param card
   * @param cardName
   * @return <code>true</code> if the transaction is successful, or <code>false</code> if the
   *         transaction failed due to obsolete sequence number.
   */
  private static boolean handleResponse(AbstractTransaction request, TransactionResponse response,
      CardFile card, String cardName) {
    if (response.isOk()) {
      if (request instanceof BalancePOJO) {
        System.out.println(response.toString());
      } else {
        System.out.println(request.toString());
      }
      System.out.flush();
      updateCardSeqNumber(card, cardName, response.getSequence() + 1);
      return true;
    } else {
      // Bank failed the transaction.
      if (card.getSequenceNumber() != response.getSequence()) {
        // Bank is telling the atm to update the sequence number.
        updateCardSeqNumber(card, cardName, response.getSequence());
      } else {
        // General failure
        System.exit(255);
      }
      return false;
    }
  }
}
