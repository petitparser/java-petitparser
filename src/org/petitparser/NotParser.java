package org.petitparser;

/**
 * The not-predicate, a parser that succeeds whenever its delegate does not, but
 * consumes no input [Parr 1994, 1995].
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class NotParser<T> extends AndParser<T> {

  NotParser(Parser<T> delegate) {
    super(delegate);
  }

  @Override
  public boolean parse(Context context) {
    return !super.parse(context);
  }

}
