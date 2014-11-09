package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * A parser that succeeds only at the end of the input stream.
 */
public class EndOfInputParser extends DelegateParser {

  protected final String message;

  public EndOfInputParser(Parser delegate, String message) {
    super(delegate);
    this.message = Objects.requireNonNull(message);
  }

  @Override
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isFailure()) {
      return result;
    }
    if (context.getPosition() == context.getBuffer().length()) {
      return result;
    }
    return result.failure(message, result.getPosition());
  }

  @Override
  protected boolean equalsProperties(Parser other) {
    return super.equalsProperties(other) &&
        Objects.equals(message, ((EndOfInputParser) other).message);
  }

  @Override
  public Parser copy() {
    return new EndOfInputParser(delegate, message);
  }
}
