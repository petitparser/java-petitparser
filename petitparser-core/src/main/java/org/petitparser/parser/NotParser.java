package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * The not-predicate, a parser that succeeds whenever its delegate does not, but
 * consumes no input [Parr 1994, 1995].
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class NotParser extends DelegateParser {

  private final String message;

  public NotParser(Parser delegate, String message) {
    super(delegate);
    this.message = message;
  }

  @Override
  public Result parse(Context context) {
    Result result = super.parse(context);
    if (result.isFailure()) {
      return context.success(null);
    } else {
      return context.failure(message);
    }
  }

  @Override
  protected boolean matchesProperties(Parser other) {
    NotParser otherNotParser = (NotParser) other;
    return super.matchesProperties(other) && message.equals(otherNotParser.message);
  }

}
