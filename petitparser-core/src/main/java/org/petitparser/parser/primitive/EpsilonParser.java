package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

/**
 * A parser that consumes nothing and always succeeds.
 */
public class EpsilonParser extends Parser {

  @Override
  public Result parseOn(Context context) {
    return context.success(null);
  }

  @Override
  public Parser copy() {
    return new EpsilonParser();
  }
}
