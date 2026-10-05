package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * A parser that consumes nothing and always fails.
 */
public class FailureParser extends Parser {

  /**
   * Construct a {@link FailureParser} that fails with the supplied {@code message}.
   */
  public static Parser withMessage(String message) {
    return new FailureParser(message);
  }

  private final String message;

  /**
   * Constructs a failure parser with a default error message.
   */
  public FailureParser() {
    this("unable to parse");
  }

  /**
   * Constructs a failure parser with the specified {@code message}.
   *
   * @param message the failure message.
   */
  public FailureParser(String message) {
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  /**
   * Returns the failure message.
   */
  public String getMessage() {
    return message;
  }

  @Override
  public Result parseOn(Context context) {
    return context.failure(message);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return -1;
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(message, ((FailureParser) other).message);
  }

  @Override
  public FailureParser copy() {
    return new FailureParser(message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
