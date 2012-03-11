package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser extends Parser {

  private final Parser delegate;

  public DelegateParser(Parser delegate) {
    this.delegate = delegate;
  }

  @Override
  public Result parse(Context context) {
    return delegate.parse(context);
  }

}
