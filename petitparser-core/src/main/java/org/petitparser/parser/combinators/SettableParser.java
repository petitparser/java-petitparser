package org.petitparser.parser.combinators;

import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.FailureParser;

/**
 * A parser that can be set to behave like another parser.
 */
public class SettableParser extends DelegateParser {

  public SettableParser(Parser delegate) {
    super(delegate);
  }

  /**
   * Constructs a {@link SettableParser} that currently refers to an {@link FailureParser}.
   */
  public static SettableParser undefined() {
    return undefined("Undefined parser");
  }

  /**
   * Constructs a {@link SettableParser} that currently refers to an {@link FailureParser} with the
   * provided {@code message}.
   */
  public static SettableParser undefined(String message) {
    return with(FailureParser.withMessage(message));
  }

  /**
   * Constructs a {@link SettableParser} referring to the supplied {@code parser}.
   */
  public static SettableParser with(Parser parser) {
    return new SettableParser(parser);
  }

  /**
   * Return the current referred parser.
   */
  public Parser get() {
    return delegate;
  }

  /**
   * Replace the current referred parser with a new {@code delegate}.
   */
  public void set(Parser delegate) {
    this.delegate = delegate;
  }

  @Override
  public Parser copy() {
    return new SettableParser(delegate);
  }
}
