package org.petitparser.context;

import org.petitparser.ParseContext;

/**
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class StringParseContext<O> extends GenericParseContext<Character, O> {

  private final CharSequence sequence;
  private int position;


  public StringParseContext(CharSequence sequence) {
    this.sequence = sequence;
    this.position = 0;
  }

  @Override
  public void push(O object) {
    // TODO Auto-generated method stub

  }

  @Override
  public O pop() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isFailure() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ParseContext<Character, O> snapshot() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void restore(ParseContext<Character, O> snapshot) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean hasNext() {
    return position < sequence.length();
  }

  @Override
  public Character next() {
    return sequence.charAt(position++);
  }

  @Override
  public Character peek() {
    return sequence.charAt(position);
  }



}
