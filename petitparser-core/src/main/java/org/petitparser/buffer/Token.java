package org.petitparser.buffer;

/**
 * Generic token interface.
 */
public interface Token {

  /**
   * Returns the input buffer.
   */
  Buffer getBuffer();

  /**
   * Returns the stop position in the input buffer.
   */
  int getStart();

  /**
   * Returns the start position in the input buffer.
   */
  int getStop();

  /**
   * Returns the parse result.
   */
  <T> T getValue();

}
