package org.petitparser;

import org.petitparser.context.Context;

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
  public Context<T> parse(Context<?> context) {
    return context.failure(message);
  }

}
