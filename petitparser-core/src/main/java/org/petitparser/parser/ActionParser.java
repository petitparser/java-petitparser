package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

import com.google.common.base.Function;

/**
 * A parser that performs a transformation with a given function on the
 * successful parse result of the delegate.
 *
 * @param <T> The type of the function argument.
 * @param <R> The type of the function result.
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ActionParser<T, R> extends DelegateParser {

  private final Function<T, R> function;

  public ActionParser(Parser delegate, Function<T, R> function) {
    super(delegate);
    this.function = function;
  }

  @Override
  public Result parse(Context context) {
    Result result = super.parse(context);
    if (result.isSuccess()) {
      return result.success(function.apply(result.<T>get()));
    } else {
      return result;
    }
  }

  protected boolean matchesProperties(Parser other) {
    return super.matchesProperties(other) && function.equals(((ActionParser) other).function);
  }

}
