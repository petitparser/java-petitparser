package org.petitparser.parser;

import org.petitparser.Parser;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that answers a flat copy of the range my delegate parses.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class FlattenParser extends AbstractParser<String> {

  private final Parser<?> delegate;

  public FlattenParser(Parser<?> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Result<String> parse(Context context) {
    Result<?> result = delegate.parse(context);
    if (result.isSuccess()) {
      CharSequence flattened = context.getBuffer().subSequence(context.getPosition(), result.getPosition());
      return result.success(flattened.toString());
    } else {
      return result.cast();
    }
  }
}
