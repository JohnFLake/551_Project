package edu.upenn.cis551.pncbank.utils;

/**
 * Utilities for printing to console, to ensure uniformity of printing.
 * 
 * @author jlautman
 *
 */
public class PrintUtils {
  /**
   * Utility function for consistently printing currencies. Must exist because the spec expects
   * 100.00 to print as 100, 0 to print as 0, 100.10 to print as 100.1, and 100.11 to print as
   * 100.11.
   * 
   * @param valAsCents The long value as cents
   * @return
   */
  public static String writeCurrency(long valAsCents) {
    long dollars = valAsCents / 100;
    int cents = (int) (valAsCents % 100);
    if (cents == 0) {
      return Long.toString(dollars);
    } else if (cents % 10 == 0) {
      return String.format("%d.%d", dollars, cents / 10);
    } else {
      return String.format("%d.%02d", dollars, cents);
    }
  }

  /**
   * Version of writeCurrency where the input is a string, to handle really large values.
   * 
   * @param valAsCents The value, as a String.
   * @return The value in dollars
   */
  public static String writeCurrency(String valAsCents) {
    if (valAsCents.length() <= 2) {
      // Balance is just in cents
      return String.format("0.%02d", Integer.parseInt(valAsCents, 10));
    } else {
      return valAsCents.substring(0, valAsCents.length() - 2) + '.'
          + valAsCents.substring(valAsCents.length() - 2);
    }
  }
}
