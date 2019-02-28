package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * A parser that succeeds only at the end of the input stream.
 */
public class EndOfInputParser extends Parser {

  protected final String message;

  public EndOfInputParser(String message) {
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  @Override
  public Result parseOn(Context context) {
    return context.getPosition() < context.getBuffer().length() ?
        context.failure(message) : context.success(null);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(message, ((EndOfInputParser) other).message);
  }

  @Override
  public EndOfInputParser copy() {
    return new EndOfInputParser(message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
