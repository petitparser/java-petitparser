package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * A parser that consumes nothing and always fails.
 */
public class FailureParser extends Parser {

  private final String message;

  public FailureParser(String message) {
    this.message = Objects.requireNonNull(message);
  }

  @Override
  public Result parseOn(Context context) {
    return context.failure(message);
  }

  @Override
  protected boolean equalsProperties(Parser other) {
    return super.equalsProperties(other) &&
        Objects.equals(message, ((FailureParser) other).message);
  }

  @Override
  public Parser copy() {
    return new FailureParser(message);
  }
}
