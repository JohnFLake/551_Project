package edu.upenn.cis551.pncbank;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import edu.upenn.cis551.pncbank.utils.InputValidator;

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
  public void testInvalidIP2() {
    String ip = "004.131.100.134";
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

  @Test
  public void testInvalidFileNameTooLong() {
    StringBuilder file = new StringBuilder();
    for (int i = 0; i < 206; i++)
      file.append("");

    assertEquals(false, InputValidator.isValidFile(file.toString()));
  }

  @Test
  public void testValidFileNameLengthLimit() {
    StringBuilder file = new StringBuilder();
    for (int i = 0; i < 205; i++)
      file.append("a");

    assertEquals(true, InputValidator.isValidFile(file.toString()));
  }

  @Test
  public void testValidAccountNamePeriod() {
    String n = ".";
    assertEquals(true, InputValidator.isValidAccountName(n));
  }

  @Test
  public void testConvertToCents1() {
    assertEquals(10011, InputValidator.convertDollarsToCents("100.11"));
  }

  @Test
  public void testConvertToCents2() {
    assertEquals(10011332, InputValidator.convertDollarsToCents("100113.32"));
  }

  @Test
  public void testConvertToCents3() {
    assertEquals(1001133200, InputValidator.convertDollarsToCents("10011332"));
  }

  @Test
  public void testConvertToCents4() {
    assertEquals(50000, InputValidator.convertDollarsToCents("500"));
  }



}
