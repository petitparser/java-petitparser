package org.petitparser.parser.combinators;

import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.FailureParser;

/**
 * A parser that can be set to behave like another parser.
 */
public class SettableParser extends DelegateParser {

  public static SettableParser undefined() {
    return undefined("Undefined parser");
  }

  public static SettableParser undefined(String message) {
    return with(FailureParser.withMessage(message));
  }

  public static SettableParser with(Parser parser) {
    return new SettableParser(parser);
  }

  public SettableParser(Parser delegate) {
    super(delegate);
  }

  public Parser get() {
    return delegate;
  }

  public void set(Parser delegate) {
    this.delegate = delegate;
  }

  @Override
  public Parser copy() {
    return new SettableParser(delegate);
  }
}
