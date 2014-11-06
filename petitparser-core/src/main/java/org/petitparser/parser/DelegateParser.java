package org.petitparser.parser;

import java.util.List;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser extends Parser {

  protected static final Parser DEFAULT_DELEGATE =
      new FailureParser("No delegate parser specified");

  protected Parser delegate;

  public DelegateParser() {
    this(DEFAULT_DELEGATE);
  }

  public DelegateParser(Parser delegate) {
    this.delegate = delegate;
  }

  @Override
  public Result parse(Context context) {
    return getDelegate().parse(context);
  }

  public Parser getDelegate() {
    return delegate;
  }

  @Override
  public void replace(Parser source, Parser target) {
    super.replace(source, target);
    if (delegate == source) {
      delegate = target;
    }
  }

  @Override
  public List<Parser> getChildren() {
    List<Parser> children = super.getChildren();
    children.add(getDelegate());
    return children;
  }

}
