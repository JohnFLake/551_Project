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
import edu.upenn.cis551.pncbank.transaction.BalancePOJO;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.DepositPOJO;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;
import edu.upenn.cis551.pncbank.transaction.WithdrawPOJO;

public class Client {
  static IEncryption<SecretKey, SecretKey> encryption = new AESEncryption();

  public static TransactionResponse sendPOJO(Object pojo, Session session) {

    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
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

      // Timeout is 10 seconds
      AtmBank.setSoTimeout(10 * 1000);

      // Send the encrypted bytes and receive a response:
      response = Session.writeToAndReadFromSocket(AtmBank, encrypted);

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
      if (tResponse == null || !tResponse.isOk()) {
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


  public static void newAccount(Session session, String accountName, int balance) {
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
    Object pojo =
        new CreateAccountPOJO(accountName, newCard.getPin(), balance, newCard.getSequenceNumber());


    // Send pojo and get response. Print it.
    TransactionResponse tResponse = sendPOJO(pojo, session);
    System.out.println(tResponse.toString());

    // Update sequence number
    if (newCard.getSequenceNumber() == tResponse.getSequence()) {
      updateCardSeqNumber(newCard, session.getCard(), tResponse.getSequence() + 1);
    } else {
      System.exit(255);
    }
  }


  public static void Deposit(Session session, String accountName, int deposit) throws Exception {

    // Check on the proper formatting of this
    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    Object pojo =
        new DepositPOJO(accountName, checkCard.getPin(), deposit, checkCard.getSequenceNumber());

    // Send pojo and get response. Print it.
    TransactionResponse tResponse = sendPOJO(pojo, session);
    // Update sequence number
    if (checkCard.getSequenceNumber() == tResponse.getSequence()) {
      updateCardSeqNumber(checkCard, session.getCard(), tResponse.getSequence() + 1);
    } else {
      System.exit(255);
    }
  }



  public static void Withdraw(Session session, String accountName, int withdraw) throws Exception {
    if (withdraw < 0) {
      System.exit(255);
    }

    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    Object pojo =
        new WithdrawPOJO(accountName, checkCard.getPin(), withdraw, checkCard.getSequenceNumber());

    // Send pojo and get response. Print it.
    TransactionResponse tResponse = sendPOJO(pojo, session);
    // Update sequence number
    if (checkCard.getSequenceNumber() == tResponse.getSequence()) {
      updateCardSeqNumber(checkCard, session.getCard(), tResponse.getSequence() + 1);
    } else {
      System.exit(255);
    }

  }



  public static void checkBalance(Session session, String accountName) throws Exception {
    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    Object pojo = new BalancePOJO(accountName, checkCard.getPin(), checkCard.getSequenceNumber());

    // Send pojo and get response. Print it.
    TransactionResponse tResponse = sendPOJO(pojo, session);
    // Update sequence number
    if (checkCard.getSequenceNumber() == tResponse.getSequence()) {
      updateCardSeqNumber(checkCard, session.getCard(), tResponse.getSequence() + 1);
    } else {
      System.exit(255);
    }
  }
}
