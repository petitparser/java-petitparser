package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

/**
 * A parser that answers a flat copy of the range my delegate parses.
 */
public class FlattenParser extends DelegateParser {

  public FlattenParser(Parser delegate) {
    super(delegate);
  }

  @Override
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isSuccess()) {
      String flattened = context.getBuffer()
          .substring(context.getPosition(), result.getPosition());
      return result.success(flattened);
    } else {
      return result;
    }
  }

  @Override
  public Parser copy() {
    return new FlattenParser(delegate);
  }
}
