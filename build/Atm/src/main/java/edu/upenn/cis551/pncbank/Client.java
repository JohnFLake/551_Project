package edu.upenn.cis551.pncbank;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.crypto.SecretKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upenn.cis551.pncbank.encryption.AESEncryption;
import edu.upenn.cis551.pncbank.encryption.Authentication;
import edu.upenn.cis551.pncbank.encryption.CardFile;
import edu.upenn.cis551.pncbank.encryption.IEncryption;
import edu.upenn.cis551.pncbank.transaction.BalancePOJO;
import edu.upenn.cis551.pncbank.transaction.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.DepositPOJO;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;
import edu.upenn.cis551.pncbank.transaction.WithdrawPOJO;

public class Client {
  static IEncryption<SecretKey, SecretKey> encryption = new AESEncryption();


  public static void newAccount(Session session, String accountName, int balance) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
    Socket AtmBank = null;

    // TODO: explain this:
    if (Integer.parseInt(Integer.toString(balance).substring(0, 1)) == 0) {
      System.exit(255);
    }

    if (balance < 10) {
      System.exit(255);
    }

    // Make a new cardfile
    CardFile newCard = new CardFile(accountName);
    Authentication.saveCardFile(session.getCard(), newCard);


    Object send =
        new CreateAccountPOJO(accountName, newCard.getPin(), balance, newCard.getSequenceNumber());

    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      try {

        // Encrypt this POJO:
        SecretKey encryptKey = session.getsecretkey();
        byte[] encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(send), encryptKey);

        // Send the encrypted bytes and receive a response:
        response = Session.writeToAndReadFromSocket(AtmBank, encrypted);

        // The response is encrypted. we need to decrypt it.
        byte[] decryptedResponse = encryption.decrypt(response, encryptKey);

        TransactionResponse tResponse =
            objectMapper.readValue(decryptedResponse, TransactionResponse.class);
        // Print response:
        System.out.println(objectMapper.writeValueAsString(tResponse));
        if (!tResponse.isOk()) {
          System.exit(255);
        }

        // Update the sequence number:
        if (newCard.getSequenceNumber() == tResponse.getSequence()) {
          newCard.setSequenceNumber(tResponse.getSequence() + 1);
          Authentication.saveCardFile(session.getCard(), newCard);
        } else {
          System.exit(255);
        }

      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        System.exit(63);
      }
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void Deposit(Session session, String accountName, int deposit) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
    Socket AtmBank = null;

    // Check on the proper formatting of this
    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    Object send =
        new DepositPOJO(accountName, checkCard.getPin(), deposit, checkCard.getSequenceNumber());

    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      try {

        // Encrypt this POJO:
        SecretKey encryptKey = session.getsecretkey();
        byte[] encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(send), encryptKey);
        response = Session.writeToAndReadFromSocket(AtmBank, encrypted);


        // The response is encrypted. we need to decrypt it.
        byte[] decryptedResponse = encryption.decrypt(response, encryptKey);


        TransactionResponse tResponse =
            objectMapper.readValue(decryptedResponse, TransactionResponse.class);
        // Print response:
        System.out.println(objectMapper.writeValueAsString(tResponse));
        if (!tResponse.isOk()) {
          System.exit(255);
        }

        if (checkCard.getSequenceNumber() == tResponse.getSequence()) {
          checkCard.setSequenceNumber(tResponse.getSequence() + 1);
          Authentication.saveCardFile(session.getCard(), checkCard);
        } else {
          System.exit(255);
        }

      } catch (Exception e) {
        e.printStackTrace();
        System.exit(63);
      }
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }



  public static void Withdraw(Session session, String accountName, int withdraw) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
    Socket AtmBank = null;

    if (withdraw < 0) {
      System.exit(255);
    }

    // Check on the proper formatting of this
    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    Object send =
        new WithdrawPOJO(accountName, checkCard.getPin(), withdraw, checkCard.getSequenceNumber());


    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      try {
        // Encrypt this POJO:
        SecretKey encryptKey = session.getsecretkey();
        byte[] encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(send), encryptKey);
        response = Session.writeToAndReadFromSocket(AtmBank, encrypted);

        // The response is encrypted. we need to decrypt it.
        byte[] decryptedResponse = encryption.decrypt(response, encryptKey);

        TransactionResponse tResponse =
            objectMapper.readValue(decryptedResponse, TransactionResponse.class);
        // Print response:
        System.out.println(objectMapper.writeValueAsString(tResponse));
        if (!tResponse.isOk()) {
          System.exit(255);
        }

        if (checkCard.getSequenceNumber() == tResponse.getSequence()) {
          checkCard.setSequenceNumber(tResponse.getSequence() + 1);
          Authentication.saveCardFile(session.getCard(), checkCard);
        } else {
          System.exit(255);
        }


      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        System.exit(63);
      }
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }



  public static void checkBalance(Session session, String accountName) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
    Socket AtmBank = null;


    File cardFile = new File(session.getCard());
    if (!cardFile.exists()) {
      System.exit(255);
    }

    CardFile checkCard = Authentication.getCardFile(session.getCard());

    Object send = new BalancePOJO(accountName, checkCard.getPin(), checkCard.getSequenceNumber());


    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      try {
        // Encrypt this POJO:
        SecretKey encryptKey = session.getsecretkey();
        byte[] encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(send), encryptKey);
        response = Session.writeToAndReadFromSocket(AtmBank, encrypted);


        // The response is encrypted. we need to decrypt it.
        byte[] decryptedResponse = encryption.decrypt(response, encryptKey);


        BalanceResponse bResponse =
            objectMapper.readValue(decryptedResponse, BalanceResponse.class);
        // Print response:
        System.out.println(objectMapper.writeValueAsString(bResponse));
        if (!bResponse.isOk()) {
          System.exit(255);
        }

        if (checkCard.getSequenceNumber() == bResponse.getSequence()) {
          checkCard.setSequenceNumber(bResponse.getSequence() + 1);
          Authentication.saveCardFile(session.getCard(), checkCard);
        } else {
          System.exit(255);
        }

      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        System.exit(63);
      }
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
