package edu.upenn.cis551.pncbank;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.crypto.SecretKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upenn.cis551.pncbank.encryption.AESEncryption;
import edu.upenn.cis551.pncbank.encryption.CardFile;
import edu.upenn.cis551.pncbank.encryption.IEncryption;
import edu.upenn.cis551.pncbank.transaction.BalancePOJO;
import edu.upenn.cis551.pncbank.transaction.BalanceResponse;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;
import edu.upenn.cis551.pncbank.transaction.TransactionResponse;

public class atmclient {
  static IEncryption<SecretKey, SecretKey> encryption = new AESEncryption();



  public static void newAccount(atmSession session, String accountName, int balance)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
    Socket AtmBank = null;

    if (Integer.parseInt(Integer.toString(balance).substring(0, 1)) == 0) {
      System.exit(255);
    }

    if (balance < 10) {
      System.exit(255);
    }

    // TODO: check that the card is being obtained properly.
    File checkcard = new File(accountName + ".card");
    CardFile newCard = new CardFile(accountName + ".card");


    Object send =
        new CreateAccountPOJO(accountName, newCard.getPin(), balance, newCard.getSequenceNumber());

    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      try {

        // Encrypt this POJO:
        SecretKey encryptKey = session.getsecretkey();
        byte[] encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(send), encryptKey);

        // Send the encrypted bytes and receive a response:
        response = atmSession.writeToAndReadFromSocket(AtmBank, encrypted);

        // The response is encrypted. we need to decrypt it.
        byte[] decryptedResponse = encryption.decrypt(response, encryptKey);

        TransactionResponse tResponse =
            objectMapper.readValue(decryptedResponse, TransactionResponse.class);
        if (!tResponse.isOk()) {
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


  public static void Deposit(atmSession session, String accountName, int deposit)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
    Socket AtmBank = null;

    if (deposit < 0) {
      System.exit(255);
    }

    // Check on the proper formatting of this

    File checkcard = new File(accountName + ".card");
    CardFile newCard = null;

    if (!checkcard.exists()) {
      System.exit(255);
    }

    newCard = new CardFile(accountName + ".card");
    Object send =
        new CreateAccountPOJO(accountName, newCard.getPin(), deposit, newCard.getSequenceNumber());
    String sendstring = objectMapper.writeValueAsString(send);

    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      try {

        // Encrypt this POJO:
        SecretKey encryptKey = session.getsecretkey();
        byte[] encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(send), encryptKey);
        response = atmSession.writeToAndReadFromSocket(AtmBank, encrypted);
        System.out.println(response);

        // The response is encrypted. we need to decrypt it.
        byte[] decryptedResponse = encryption.decrypt(response, encryptKey);
        System.out.println(decryptedResponse);

        TransactionResponse tResponse =
            objectMapper.readValue(decryptedResponse, TransactionResponse.class);
        if (!tResponse.isOk()) {
          System.exit(255);
        }

        else {
          newCard.setSequenceNumber(tResponse.getSequence());
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
    System.out.println(sendstring);

  }



  public static void Withdraw(atmSession session, String accountName, int withdraw)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
    Socket AtmBank = null;

    if (withdraw < 0) {
      System.exit(255);
    }

    // Check on the proper formatting of this

    File checkcard = new File(accountName + ".card");
    CardFile newCard = null;

    if (!checkcard.exists()) {
      System.exit(255);
    }

    newCard = new CardFile(accountName + ".card");
    Object send =
        new CreateAccountPOJO(accountName, newCard.getPin(), withdraw, newCard.getSequenceNumber());
    String sendstring = objectMapper.writeValueAsString(send);

    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      try {
        // Encrypt this POJO:
        SecretKey encryptKey = session.getsecretkey();
        byte[] encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(send), encryptKey);
        response = atmSession.writeToAndReadFromSocket(AtmBank, encrypted);
        System.out.println(response);

        // The response is encrypted. we need to decrypt it.
        byte[] decryptedResponse = encryption.decrypt(response, encryptKey);
        System.out.println(decryptedResponse);

        TransactionResponse tResponse =
            objectMapper.readValue(decryptedResponse, TransactionResponse.class);
        if (!tResponse.isOk()) {
          System.exit(255);
        }

        else {
          newCard.setSequenceNumber(tResponse.getSequence());
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
    System.out.println(sendstring);

  }



  public static void checkBalance(atmSession session, String accountName)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    byte[] response = null;
    Socket AtmBank = null;


    // Check on the proper formatting of this

    File checkcard = new File(accountName + ".card");
    CardFile newCard = null;

    if (!checkcard.exists()) {
      System.exit(255);
    }

    newCard = new CardFile(accountName + ".card");

    Object send = new BalancePOJO(accountName, newCard.getPin(), newCard.getSequenceNumber());
    String sendstring = objectMapper.writeValueAsString(send);

    try {
      AtmBank = new Socket(session.getIP(), session.getPort());
      try {
        // Encrypt this POJO:
        SecretKey encryptKey = session.getsecretkey();
        byte[] encrypted = encryption.encrypt(objectMapper.writeValueAsBytes(send), encryptKey);
        response = atmSession.writeToAndReadFromSocket(AtmBank, encrypted);
        System.out.println(response);

        // The response is encrypted. we need to decrypt it.
        byte[] decryptedResponse = encryption.decrypt(response, encryptKey);
        System.out.println(decryptedResponse);

        BalanceResponse bResponse = objectMapper.readValue(response, BalanceResponse.class);
        if (!bResponse.isOk()) {
          System.exit(255);
        }

        else {
          newCard.setSequenceNumber(bResponse.getSequence());
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
    System.out.println(sendstring);
  }
}
