package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

/**
 * A parser that reports the current input position.
 */
public class PositionParser extends Parser {

  /**
   * Reusable singleton instance of {@link PositionParser}.
   */
  public static final PositionParser INSTANCE = new PositionParser();

  @Override
  public Result parseOn(Context context) {
    return context.success(context.getPosition());
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return position;
  }

  @Override
  public PositionParser copy() {
    return new PositionParser();
  }
}
