package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that consumes nothing and always succeeds.
 */
public class EpsilonParser extends Parser {

  @Override
  public Result parse(Context context) {
    return context.success(null);
  }

}
