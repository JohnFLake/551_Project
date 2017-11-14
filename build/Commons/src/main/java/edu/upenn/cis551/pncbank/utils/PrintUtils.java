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
}
