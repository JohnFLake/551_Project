package edu.upenn.cis551.pncbank.bank.atmservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  public AtmService(int port) throws IOException {
    socketBinding = new ServerSocket(port);
    mapper = new ObjectMapper();
  }

  /**
   * Handles the next request from the port.
   * 
   * @return
   * @throws IOException
   */
  public void handleNextRequest() throws IOException {
    Socket s = socketBinding.accept();
    InputStream is = s.getInputStream();
    // TODO Decryption of the data from the atm goes here
    try {
      AbstractTransaction at = mapper.readValue(is, AbstractTransaction.class);
    } catch (IOException e) {
      // TODO
    }
    // TODO Do stuff with the account store here
    
    String response = "{}";
    // TODO actually encrypt
    byte[] encryptedResponse = response.getBytes();
    OutputStream os = s.getOutputStream();
    os.write(encryptedResponse);
    os.flush();
    s.close();
  }
}
