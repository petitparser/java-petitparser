package org.petitparser;

/**
 * The and-predicate, a parser that succeeds whenever its delegate does, but
 * does not consume the input stream [Parr 1994, 1995].
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class AndParser<T> extends DelegateParser<T> {

  AndParser(Parser<T> delegate) {
    super(delegate);
  }

  @Override
  public boolean parse(Context context) {
    int position = context.position;
    boolean result = super.parse(context);
    context.position = position;
    return result;
  }

}
