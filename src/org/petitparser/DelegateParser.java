package org.petitparser;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser<T> extends AbstractParser<T> {

  final Parser<T> delegate;

  DelegateParser(Parser<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean parse(Context context) {
    return delegate.parse(context);
  }

}
