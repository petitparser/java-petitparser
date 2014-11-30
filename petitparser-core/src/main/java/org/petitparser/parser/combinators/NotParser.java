package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * The not-predicate, a parser that succeeds whenever its delegate does not, but consumes no input
 * [Parr 1994, 1995].
 */
public class NotParser extends DelegateParser {

  protected final String message;

  public NotParser(Parser delegate, String message) {
    super(delegate);
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  @Override
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isFailure()) {
      return context.success(null);
    } else {
      return context.failure(message);
    }
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) && Objects.equals(message, ((NotParser) other).message);
  }

  @Override
  public Parser copy() {
    return new NotParser(delegate, message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
