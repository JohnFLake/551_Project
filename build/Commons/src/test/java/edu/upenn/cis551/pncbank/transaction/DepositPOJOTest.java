package edu.upenn.cis551.pncbank.transaction;

import org.junit.Assert;
import org.junit.Test;

public class DepositPOJOTest {

  @Test
  public void test_toString_0cents() {
    DepositPOJO test = new DepositPOJO("testacct", "valid", 100l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"deposit\":1}", test.toString());
  }

  @Test
  public void test_toString_10cents() {
    DepositPOJO test = new DepositPOJO("testacct", "valid", 110l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"deposit\":1.1}", test.toString());
  }

  @Test
  public void test_toString_11cents() {
    DepositPOJO test = new DepositPOJO("testacct", "valid", 111l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"deposit\":1.11}", test.toString());
  }

  @Test
  public void test_toString_largestNumber() {
    DepositPOJO test =
        new DepositPOJO("testacct", "valid", AbstractTransaction.maxCurrencyValue, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"deposit\":4294967295.99}", test.toString());
  }

}
