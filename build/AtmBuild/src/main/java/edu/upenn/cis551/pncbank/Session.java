package edu.upenn.cis551.pncbank;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.crypto.SecretKey;

public class Session {
  private String IP;
  private int port;
  private SecretKey auth;
  private String card;


  public Session(String IP, int port, SecretKey auth, String c) {
    this.IP = IP;
    this.port = port;
    this.auth = auth;
    this.card = c;

  }

  public String getCard() {
    return card;
  }


  public String getIP() {
    return IP;
  }

  public int getPort() {
    return port;
  }

  public SecretKey getsecretkey() {
    return auth;
  }


  /**
   * Write bytes to the server and get back a byte array as a response
   * 
   * @param socket
   * @param writeTo
   * @return
   * @throws Exception
   */
  public static byte[] writeToAndReadFromSocket(Socket socket, byte[] writeTo) throws IOException {

    // write raw bytes to socket.
    writeToSocket(socket, writeTo);
    socket.shutdownOutput();

    // read raw bytes from socket:
    InputStream is = socket.getInputStream();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int next = is.read();
    while (next > -1) {
      bos.write(next);
      next = is.read();
    }
    bos.flush();
    byte[] result = bos.toByteArray();

    socket.close();
    return result;

  }

  /**
   * Writes bytes to a socket's output.
   * 
   * @param socket
   * @param writeTo The bytes to write.
   * @throws IOException On some IO failure, such as the socket being closed.
   */
  public static void writeToSocket(Socket socket, byte[] writeTo) throws IOException {
    OutputStream os = socket.getOutputStream();
    os.write(writeTo);
    os.flush();
  }

}
