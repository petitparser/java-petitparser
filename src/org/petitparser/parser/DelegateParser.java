package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser extends Parser {

  private Parser delegate;

  public DelegateParser() {
    this(new FailureParser("No delegate parser specified"));
  }

  public DelegateParser(Parser delegate) {
    this.delegate = delegate;
  }

  @Override
  public Result parse(Context context) {
    return delegate.parse(context);
  }

  public Parser getDelegate() {
    return delegate;
  }

  public void setDelegate(Parser delegate) {
    this.delegate = delegate;
  }

  @Override
  public void replace(Parser source, Parser target) {
    if (delegate == source) {
      delegate = target;
    }
  }

  @Override
  public Parser[] children() {
    return new Parser[] { delegate };
  }

}
