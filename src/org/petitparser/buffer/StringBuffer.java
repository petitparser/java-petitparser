package org.petitparser.buffer;

/**
 * Buffer backed by a {@link CharSequence}.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class StringBuffer implements Buffer {

  private final CharSequence sequence;

  public StringBuffer(CharSequence sequence) {
    this.sequence = sequence;
  }

  @Override
  public int size() {
    return sequence.length();
  }

  @Override
  public Object at(int index) {
    return sequence.charAt(index);
  }

  @Override
  public char charAt(int index) {
    return sequence.charAt(index);
  }

}
