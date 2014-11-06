package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that consumes nothing and always fails.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class FailureParser extends Parser {

  private final String message;

  public FailureParser(String message) {
    this.message = message;
  }

  @Override
  public Result parse(Context context) {
    return context.failure(message);
  }

  @Override
  protected boolean matchesProperties(Parser other) {
    FailureParser otherFailureParser = (FailureParser) other;
    return super.matchesProperties(other) && message.equals(otherFailureParser.message);
  }

}
