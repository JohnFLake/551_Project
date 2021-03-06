package edu.upenn.cis551.pncbank.encryption;

import java.util.UUID;
import java.util.Random;

public class CardFile {
  
  private String pin; 
  private String accountName; 
  private long sequenceNumber; 
  
  public CardFile() {
    pin = UUID.randomUUID().toString(); 
    Random r = new Random(); 
    sequenceNumber = r.nextInt(); 
  }
  
  
  public CardFile(String name) {
    accountName = name; 
    pin = UUID.randomUUID().toString(); 
    Random r = new Random(); 
    sequenceNumber = r.nextInt(); 
  }

  


  public String getPin() {
    return pin;
  }

  public String getAccountName() {
    return accountName;
  }
  
  public void setAccountName(String name) {
    accountName = name; 
  }


  public long getSequenceNumber() {
    return sequenceNumber;
  }


  public void setSequenceNumber(long sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }
  
}
