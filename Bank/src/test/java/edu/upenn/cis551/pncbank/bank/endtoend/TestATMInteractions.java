package edu.upenn.cis551.pncbank.bank.endtoend;

import org.junit.Before;
import edu.upenn.cis551.pncbank.Bank;

public class TestATMInteractions {
  private Bank bank;
  @Before
  public void setUp() {
    bank = new Bank(); // Doesn't work since it isn't an object.
  }
}
