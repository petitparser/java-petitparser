package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

/**
 * A parser that answers a flat copy of the range my delegate parses.
 */
public class FlattenParser extends DelegateParser {

  private FlattenJoiner joiner;
  protected final String message;

  public FlattenParser(Parser delegate) {
    this(delegate, null);
  }

  public FlattenParser(Parser delegate, String message) {
    this(delegate,message, new DefaultFlattenJoiner());
  }

  public FlattenParser(Parser delegate, String message, FlattenJoiner joiner) {
    super(delegate);
    this.message = message;
    this.joiner = joiner;
  }

  @Override
  public Result parseOn(Context context) {
    if (message == null) {
      Result result = delegate.parseOn(context);
      if (result.isSuccess()) {
        return joiner.join(context, result);
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

      Result result = delegate.parseOn(context);
      return joiner.join(context, result);
    }
  }

  @Override
  public FlattenParser copy() {
    return new FlattenParser(delegate, message);
  }
}
