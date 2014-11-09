package org.petitparser.parser.combinators;

import org.petitparser.parser.Parser;

/**
 * A parser that can be set to behave like another parser.
 */
public class SetableParser extends DelegateParser {

  public SetableParser(Parser delegate) {
    super(delegate);
  }

  public Parser getDelegate() {
    return delegate;
  }

  public void setDelegate(Parser delegate) {
    this.delegate = delegate;
  }

  @Override
  public Parser copy() {
    return new SetableParser(delegate);
  }
}
