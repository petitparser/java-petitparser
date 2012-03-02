package org.petitparser;

import org.petitparser.context.Context;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser<T> extends AbstractParser<T> {

  private final Parser<T> delegate;

  public DelegateParser(Parser<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Context<T> parse(Context<?> context) {
    return delegate.parse(context);
  }

}
