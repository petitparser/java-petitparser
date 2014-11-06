package org.petitparser.buffer;

/**
 * Generic buffer interface to abstract the data source from the parsers.
 */
public interface Buffer {

  /**
   * Returns the size of elements of this buffer.
   */
  int size();

  /**
   * Returns the object at position {@code index} in this buffer.
   */
  Object at(int index);

  /**
   * Returns the character at position {@code index} in this buffer, throws an
   * error if this is not a character buffer.
   */
  char charAt(int index);

  /**
   * Returns a {@link String} between {@code start} and {@code stop}, throws an
   * error if this is not a character buffer.
   */
  String subSequence(int start, int stop);

  /**
   * Creates a token on a range of the buffer.
   */
  Token newToken(int start, int stop, Object value);

}
