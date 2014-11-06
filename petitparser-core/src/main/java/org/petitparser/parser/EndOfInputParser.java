package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that succeeds only at the end of the input stream.
 */
public class EndOfInputParser extends DelegateParser {

  private final String message;

  public EndOfInputParser(Parser delegate, String message) {
    super(delegate);
    this.message = message;
  }

  @Override
  public Result parse(Context context) {
    Result result = super.parse(context);
    if (result.isFailure() || result.atEnd()) {
      return result;
    }
    return result.failure(message, result.getPosition());
  }

  @Override
  protected boolean matchesProperties(Parser other) {
    EndOfInputParser otherEndOfInputParser = (EndOfInputParser) other;
    return super.matchesProperties(other) && message.equals(otherEndOfInputParser.message);
  }

}
