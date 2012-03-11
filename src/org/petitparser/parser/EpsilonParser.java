package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that consumes nothing and always succeeds.
 * 
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class EpsilonParser<T> extends Parser<T> {

  @Override
  public Result<T> parse(Context context) {
    return context.success(null);
  }

}
