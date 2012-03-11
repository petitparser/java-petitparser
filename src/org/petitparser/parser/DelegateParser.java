package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that delegates to another one.
 * 
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser<T> extends Parser<T> {

  private final Parser<T> delegate;

  public DelegateParser(Parser<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Result<T> parse(Context context) {
    return delegate.parse(context);
  }

}
