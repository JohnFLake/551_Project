package edu.upenn.cis551.pncbank;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AtmTest {

  @Test
  public void testInvalidFilePeriod() {
    String invalidFile = ".";
    assertEquals(false, Atm.isValidFile(invalidFile));
  }

  @Test
  public void testValidFilePeriod() {
    String invalidFile = "bank.auth";
    assertEquals(true, Atm.isValidFile(invalidFile));
  }

}
