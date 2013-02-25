package org.petitparser.buffer;

/**
 * Token backed by a {@link StringBuffer}.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class StringToken implements Token {

  private final Buffer buffer;
  private final int start;
  private final int stop;
  private final Object value;

  public StringToken(Buffer buffer, int start, int stop, Object value) {
    this.buffer = buffer;
    this.start = start;
    this.stop = stop;
    this.value = value;
  }

  @Override
  public Buffer getBuffer() {
    return buffer;
  }

  @Override
  public int getStart() {
    return start;
  }

  @Override
  public int getStop() {
    return stop;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getValue() {
    return (T) value;
  }

  @Override
  public String toString() {
    return "Token[" + start + ", " + stop + "](" + value.toString() + ")";
  }

}
