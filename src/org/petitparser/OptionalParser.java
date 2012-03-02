package org.petitparser;

import org.petitparser.context.Context;

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
  public Context<T> parse(Context<?> context) {
    Context<T> result = super.parse(context);
    return result.isFailure() ? context.<T> success(null) : result;
  }

}