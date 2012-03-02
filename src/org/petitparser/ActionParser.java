package org.petitparser;

import org.petitparser.context.Context;
import org.petitparser.utils.Function;

/**
 * A parser that performs a transformation with a given function on the
 * successful parse result of the delegate.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ActionParser<T, S> extends AbstractParser<S> {

  private final Parser<T> parser;
  private final Function<T, S> function;

  public ActionParser(Parser<T> parser, Function<T, S> function) {
    this.parser = parser;
    this.function = function;
  }

  @Override
  public Context<S> parse(Context<?> context) {
    Context<T> result = parser.parse(context);
    if (result.isSuccess()) {
      return result.success(function.apply(result.get()));
    } else {
      return result.cast();
    }
  }

}
