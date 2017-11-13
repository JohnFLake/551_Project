package edu.upenn.cis551.pncbank.transaction;

import org.junit.Assert;
import org.junit.Test;

public class WithdrawPOJOTest {

  @Test
  public void test_toString_0cents() {
    WithdrawPOJO test = new WithdrawPOJO("testacct", "valid", 100l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"withdraw\":1}", test.toString());
  }

  @Test
  public void test_toString_10cents() {
    WithdrawPOJO test = new WithdrawPOJO("testacct", "valid", 110l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"withdraw\":1.1}", test.toString());
  }

  @Test
  public void test_toString_11cents() {
    WithdrawPOJO test = new WithdrawPOJO("testacct", "valid", 111l, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"withdraw\":1.11}", test.toString());
  }

  @Test
  public void test_toString_largestNumber() {
    WithdrawPOJO test =
        new WithdrawPOJO("testacct", "valid", AbstractTransaction.maxCurrencyValue, 12345);
    Assert.assertEquals("{\"account\":\"testacct\",\"withdraw\":4294967295.99}", test.toString());
  }

}
