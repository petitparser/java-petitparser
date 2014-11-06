package org.petitparser.buffer;

/**
 * Buffer backed by a {@link CharSequence}.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class StringBuffer implements Buffer {

  private final String string;

  public StringBuffer(String sequence) {
    this.string = sequence;
  }

  @Override
  public int size() {
    return string.length();
  }

  @Override
  public Object at(int index) {
    return string.charAt(index);
  }

  @Override
  public char charAt(int index) {
    return string.charAt(index);
  }

  @Override
  public String subSequence(int start, int stop) {
    return string.substring(start, stop);
  }

  @Override
  public Token newToken(int start, int stop, Object value) {
    return new StringToken(this, start, stop, value);
  }
}
