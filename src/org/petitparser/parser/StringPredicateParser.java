package org.petitparser.parser;

import org.petitparser.buffer.Buffer;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * Parses a sequence of characters.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class StringPredicateParser extends AbstractParser<String> {

  public interface StringPredicate {
    boolean apply(String argument);
  }

  private final int size;
  private final StringPredicate predicate;
  private final String message;

  public StringPredicateParser(int size, StringPredicate predicate, String message) {
    this.size = size;
    this.predicate = predicate;
    this.message = message;
  }

  @Override
  public Result<String> parse(Context context) {
    int start = context.getPosition();
    int stop = start + size;
    Buffer buffer = context.getBuffer();
    if (stop <= buffer.size()) {
      String result = buffer.subSequence(start, stop);
      if (predicate.apply(result)) {
        return context.success(result, stop);
      }
    }
    return context.failure(message);
  }

}
