package org.petitparser;

import org.petitparser.context.Context;

/**
 * A parser that consumes nothing and always succeeds.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class EpsilonParser<T> extends AbstractParser<T> {

  @Override
  public Context<T> parse(Context<?> context) {
    return context.success(null);
  }

}
