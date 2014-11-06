package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that answers a flat copy of the range my delegate parses.
 */
public class FlattenParser extends DelegateParser {

  public FlattenParser(Parser delegate) {
    super(delegate);
  }

  @Override
  public Result parse(Context context) {
    Result result = super.parse(context);
    if (result.isSuccess()) {
      String flattened = context.getBuffer()
          .subSequence(context.getPosition(), result.getPosition());
      return result.success(flattened);
    } else {
      return result;
    }
  }
}
