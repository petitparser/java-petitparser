package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that consumes nothing and always fails.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class FailureParser<T> extends AbstractParser<T> {

  private final String message;

  public FailureParser(String message) {
    this.message = message;
  }

  @Override
  public Result<T> parse(Context context) {
    return context.failure(message);
  }

}
