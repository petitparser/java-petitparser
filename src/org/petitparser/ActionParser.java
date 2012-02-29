package org.petitparser;

import org.petitparser.utils.Function;

/**
 * A parser that performs a transformation with a given function on the
 * successful parse result of the delegate.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ActionParser<T, S> extends DelegateParser<T> {

  final Function<T, S> function;

  ActionParser(Parser<T> delegate, Function<T, S> function) {
    super(delegate);
    this.function = function;
  }

  @Override
  public boolean parse(Context context) {
    boolean success = super.parse(context);
    if (success) {
      @SuppressWarnings("unchecked")
      T result = (T) context.result;
      context.result = function.apply(result);
    }
    return success;
  }

}
