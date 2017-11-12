package edu.upenn.cis551.pncbank.encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Authentication {



  /**
   * Save a cardfile object to disc with the given file name.
   * 
   * @param fileName
   * @param card
   * @throws Exception
   */
  public static void saveCardFile(String fileName, CardFile card) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.writeValue(new File(fileName), card);
  }


  /**
   * Given a file name, return the cardfile object associated with it.
   * 
   * @param fileName
   * @return
   * @throws Exception
   */
  public static CardFile getCardFile(String fileName) throws Exception {
    String json;
    // Read base64 key string from the given file
    try {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
      }
      json = sb.toString();
      br.close();
    } catch (FileNotFoundException e) {
      throw e;
    }
    ObjectMapper objectMapper = new ObjectMapper();
    CardFile card = objectMapper.readValue(json, CardFile.class);
    return card;
  }

  /**
   * Create an AES key, and save it to a file as a base64 encoded string.
   * 
   * @param filename
   * @return
   */
  public static SecretKey generateAuthFile(String filename)
      throws EncryptionException, IOException {

    // Generate AES Key
    SecretKey key;
    AESEncryption enc = new AESEncryption();
    key = enc.generateKey().getEncryptionKey();

    // Take AES key and convert it to a base64 string.
    File authFile = new File(filename);
    if (!authFile.createNewFile()) {
      // File already existed
      throw new IOException("Auth file of that name already exists.");
    }

    try (Writer writer = new BufferedWriter(new FileWriter(filename))) {
      String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
      writer.write(encodedKey);
      writer.flush();
    } catch (IOException e) {
      throw e;
    }

    return key;
  }

  /**
   * Get an AES key object from an auth file.
   * 
   * @param filename
   * @return
   */
  public static SecretKey getAESKeyFromAuthFile(String filename) throws Exception {
    String keyString;

    // Read base64 key string from the given file
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
      }
      keyString = sb.toString();
      br.close();
    } catch (FileNotFoundException e) {
      throw e;
    }

    // Take the string and convert it to a secret key.
    byte[] key = Base64.getDecoder().decode(keyString.getBytes());
    // rebuild key using SecretKeySpec
    SecretKey aesKey = new SecretKeySpec(key, 0, key.length, "AES");
    return aesKey;
  }

}
