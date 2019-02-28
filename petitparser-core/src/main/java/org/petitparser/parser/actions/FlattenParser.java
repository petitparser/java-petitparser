package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

/**
 * A parser that answers a flat copy of the range my delegate parses.
 */
public class FlattenParser extends DelegateParser {

  protected final String message;

  public FlattenParser(Parser delegate) {
    this(delegate, null);
  }

  public FlattenParser(Parser delegate, String message) {
    super(delegate);
    this.message = message;
  }

  @Override
  public Result parseOn(Context context) {
    if (message == null) {
      Result result = delegate.parseOn(context);
      if (result.isSuccess()) {
        String flattened = context.getBuffer()
            .substring(context.getPosition(), result.getPosition());
        return result.success(flattened);
      } else {
        return result;
      }
    } else {
      // If we have a message we can switch to fast mode.
      int position =
          delegate.fastParseOn(context.getBuffer(), context.getPosition());
      if (position < 0) {
        return context.failure(message);
      }
      String output =
          context.getBuffer().substring(context.getPosition(), position);
      return context.success(output, position);
    }
  }

  @Override
  public FlattenParser copy() {
    return new FlattenParser(delegate, message);
  }
}
