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

  private FailureParser(String message) {
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  /**
   * Construct a {@link FailureParser} that fails with the supplied {@code message}.
   */
  public static Parser withMessage(String message) {
    return new FailureParser(message);
  }

  @Override
  public Result parseOn(Context context) {
    return context.failure(message);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(message, ((FailureParser) other).message);
  }

  @Override
  public Parser copy() {
    return new FailureParser(message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
