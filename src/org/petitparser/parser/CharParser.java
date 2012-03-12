package org.petitparser.parser;

import org.petitparser.buffer.Buffer;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * Parses a single character satisfying a predicate.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class CharParser extends Parser {

  public interface CharPredicate {
    boolean apply(char argument);
  }

  private final CharPredicate predicate;
  private final String message;

  public CharParser(CharPredicate predicate, String message) {
    this.predicate = predicate;
    this.message = message;
  }

  @Override
  public Result parse(Context context) {
    Buffer buffer = context.getBuffer();
    if (context.getPosition() < buffer.size()) {
      char result = buffer.charAt(context.getPosition());
      if (predicate.apply(result)) {
        return context.success(result, context.getPosition() + 1);
      }
    }
    return context.failure(message);
  }

  @Override
  public Parser negate(String message) {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char argument) {
        return !predicate.apply(argument);
      }
    }, message);
  }

}
