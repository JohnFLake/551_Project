package edu.upenn.cis551.pncbank;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.crypto.SecretKey;

public class atmSession {
  private static String IP;
  private static int port;
  private static SecretKey auth;

  public atmSession(String IP, int port, SecretKey auth) {
    this.IP = IP;
    this.port = port;
    this.auth = auth;
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
  public static byte[] writeToAndReadFromSocket(Socket socket, byte[] writeTo) throws Exception {
    try (OutputStream os = socket.getOutputStream()) {

      // write raw bytes to socket.
      os.write(writeTo);
      os.flush();
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

    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
  }



}
