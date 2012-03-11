package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that optionally parsers its delegate, or answers nil.
 * 
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class OptionalParser<T> extends DelegateParser<T> {

  public OptionalParser(Parser<T> parser) {
    super(parser);
  }

  @Override
  public Result<T> parse(Context context) {
    Result<T> result = super.parse(context);
    if (result.isSuccess()) {
      return result;
    } else {
      return context.success(null);
    }
  }

}
