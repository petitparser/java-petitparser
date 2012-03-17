package org.petitparser.parser;

import org.petitparser.buffer.Buffer;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

import com.google.common.base.CharMatcher;

/**
 * Parses a single character satisfying a predicate.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class CharParser extends Parser {

  private final CharMatcher matcher;
  private final String message;

  public CharParser(CharMatcher matcher, String message) {
    this.matcher = matcher;
    this.message = message;
  }

  @Override
  public Result parse(Context context) {
    Buffer buffer = context.getBuffer();
    if (context.getPosition() < buffer.size()) {
      char result = buffer.charAt(context.getPosition());
      if (matcher.apply(result)) {
        return context.success(result, context.getPosition() + 1);
      }
    }
    return context.failure(message);
  }

  @Override
  public Parser negate(String message) {
    return new CharParser(matcher.negate(), message);
  }

}
