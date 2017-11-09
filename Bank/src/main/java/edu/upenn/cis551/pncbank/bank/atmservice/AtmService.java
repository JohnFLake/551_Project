package edu.upenn.cis551.pncbank.bank.atmservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.transaction.AbstractTransaction;

/**
 * Accepts requests from an ATM, verifies that they are from an ATM, and then passes along any
 * transaction requests.
 * 
 * @author jlautman
 *
 */
public class AtmService {
  private ServerSocket socketBinding;
  private ObjectMapper mapper;
  private String aesKey;

  public AtmService(int port, String aesKey) throws IOException {
    socketBinding = new ServerSocket(port);
    mapper = new ObjectMapper();
    this.aesKey = aesKey;
  }

  /**
   * Handles the next request from the port.
   * 
   */
  public void handleNextRequest(AccountManager am) {
    try (Socket s = socketBinding.accept()) {
      InputStream is = s.getInputStream();
      // TODO Decryption of the data from the atm goes here
      try {
        AbstractTransaction at = mapper.readValue(is, AbstractTransaction.class);
      } catch (IOException e) {
        // TODO replace with the correct error message
        sendEncryptedResponse(s.getOutputStream(), "failure");
        return;
      }
      // TODO Do stuff with the account manager here and build the actual response
      String response = "{}";

      sendEncryptedResponse(s.getOutputStream(), response);
      s.close();
    } catch (IOException e) {
      // TODO log it?
    }
  }

  private void sendEncryptedResponse(OutputStream os, String unencrypted) throws IOException {
    // TODO replace this with the real encryption
    byte[] data = unencrypted.getBytes();
    os.write(data);
    os.flush();
  }
}
