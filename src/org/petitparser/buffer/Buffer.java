package org.petitparser.buffer;

/**
 * Generic buffer interface to abstract the data source from the parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
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

}
