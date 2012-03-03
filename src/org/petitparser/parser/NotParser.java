package org.petitparser.parser;

import org.petitparser.Parser;
import org.petitparser.context.Context;

/**
 * The not-predicate, a parser that succeeds whenever its delegate does not, but
 * consumes no input [Parr 1994, 1995].
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class NotParser<T> extends DelegateParser<T> {

  private final String message;

  public NotParser(Parser<T> delegate, String message) {
    super(delegate);
    this.message = message;
  }

  @Override
  public Context<T> parse(Context<?> context) {
    Context<T> result = super.parse(context);
    if (result.isFailure()) {
      return context.success(null);
    } else {
      return result.failure(message);
    }
  }

}
