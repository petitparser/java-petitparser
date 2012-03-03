package org.petitparser.parser;

import org.petitparser.buffer.Buffer;
import org.petitparser.context.Context;

/**
 * Parses a sequence of characters.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class CharPredicateParser extends AbstractParser<Character> {

  public interface CharPredicate {
    boolean apply(char argument);
  }

  private final CharPredicate predicate;
  private final String message;

  public CharPredicateParser(CharPredicate predicate, String message) {
    this.predicate = predicate;
    this.message = message;
  }

  @Override
  public Context<Character> parse(Context<?> context) {
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
  public AbstractParser<Character> negate() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char argument) {
        return predicate.apply(argument);
      }
    }, "no " + message);
  }

}
