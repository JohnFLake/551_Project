package edu.upenn.cis551.pncbank;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import javax.crypto.SecretKey;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upenn.cis551.pncbank.bank.AccountManager;
import edu.upenn.cis551.pncbank.encryption.AESEncryption;
import edu.upenn.cis551.pncbank.encryption.EncryptionException;
import edu.upenn.cis551.pncbank.encryption.EncryptionPair;
import edu.upenn.cis551.pncbank.transaction.CreateAccountPOJO;

public class BankTest {
  private final AESEncryption aes = new AESEncryption();

  private IMocksControl mockMaker;
  private InputStream is;
  private OutputStream os;
  private Socket s;

  private Random random;

  @Before
  public void setup() throws IOException {
    mockMaker = EasyMock.createControl();
    is = mockMaker.createMock(InputStream.class);
    os = mockMaker.createMock(OutputStream.class);
    s = mockMaker.createMock(Socket.class);
    EasyMock.expect(s.getInputStream()).andReturn(is);
    EasyMock.expect(s.getOutputStream()).andReturn(os);
    is.close();
    os.close();
    s.close();
    random = new Random();
  }

  @After
  public void tearDown() {
    mockMaker.verify();
  }

  // @Test
  public void test_createAccountCommand() throws EncryptionException, IOException {

    EncryptionPair<SecretKey, SecretKey> keys = aes.generateKey();
    AccountManager am = new AccountManager();
    Bank bank = new Bank(3000, keys.getEncryptionKey(), am);
    String validator = "really really big string";
    // $400.00
    long balance = 40000;
    long sequenceNumber = random.nextLong();

    CreateAccountPOJO create =
        new CreateAccountPOJO("testAccount", validator, balance, sequenceNumber);

    // Pretend to be an atm and create the request string
    ObjectMapper m = new ObjectMapper();
    final byte[] reqEncrypted = aes.encrypt(m.writeValueAsBytes(create), keys.getEncryptionKey());
    final Capture<byte[]> readBuffer = Capture.newInstance();
    InputStream delegator = new InputStream() {
      private int reqOffset = 0;

      @Override
      public int read() throws IOException {
        if (reqOffset >= reqEncrypted.length) {
          return -1;
        } else {
          return reqEncrypted[reqOffset++];
        }
      }
    };
    EasyMock.expect(is.read(EasyMock.capture(readBuffer))).andDelegateTo(delegator).anyTimes();

    mockMaker.replay();
    // Run
    bank.handleTransaction(s);
    bank.close();

    // Verify
  }
}
