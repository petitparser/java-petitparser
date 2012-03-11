package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * The and-predicate, a parser that succeeds whenever its delegate does, but
 * does not consume the input stream [Parr 1994, 1995].
 * 
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class AndParser<T> extends DelegateParser<T> {

  public AndParser(Parser<T> delegate) {
    super(delegate);
  }

  @Override
  public Result<T> parse(Context context) {
    Result<T> result = super.parse(context);
    if (result.isSuccess()) {
      return context.success(result.get());
    } else {
      return result;
    }
  }

}
