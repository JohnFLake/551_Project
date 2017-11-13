package edu.upenn.cis551.pncbank;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AtmTest {

  @Test
  public void testInvalidFilePeriod() {
    String invalidFile = ".";
    assertEquals(false, InputValidator.isValidFile(invalidFile));
  }

  @Test
  public void testInvalidFileTwoPeriods() {
    String invalidFile = "..";
    assertEquals(false, InputValidator.isValidFile(invalidFile));
  }

  @Test
  public void testInvalidFileNull() {
    String invalidFile = null;
    assertEquals(false, InputValidator.isValidFile(invalidFile));
  }

  @Test
  public void testValidFileDefault() {
    String file = "bank.auth";
    assertEquals(true, InputValidator.isValidFile(file));
  }

  @Test
  public void testInvalidIP() {
    String ip = "264.131.453.134";
    assertEquals(false, InputValidator.isValidIP(ip));
  }


  @Test
  public void testValidIPLocal() {
    String ip = "127.0.0.1";
    assertEquals(true, InputValidator.isValidIP(ip));
  }

  @Test
  public void testValidIP() {
    String ip = "0.0.0.0";
    assertEquals(true, InputValidator.isValidIP(ip));
  }


}
