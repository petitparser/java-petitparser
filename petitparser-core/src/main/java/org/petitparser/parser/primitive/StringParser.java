package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Parses a sequence of characters.
 */
public class StringParser extends Parser {

  /**
   * Construct a parser that accepts the provides {@link String} {@code value}.
   */
  public static Parser of(String value) {
    return of(value, value + " expected");
  }

  /**
   * Construct a parser that accepts the provides {@link String} {@code value
   * }, and that fails with
   * the error provided error {@code message}.
   */
  public static Parser of(String value, String message) {
    return new StringParser(value.length(), value::equals, message);
  }

  /**
   * Construct a parser that accepts the provides {@link String} {@code value
   * } case insensitive.
   */
  public static Parser ofIgnoringCase(String value) {
    return ofIgnoringCase(value, value + " expected");
  }

  /**
   * Construct a parser that accepts the provides {@link String} {@code value
   * } case insensitive, and
   * that fails with the error provided error {@code message}.
   */
  public static Parser ofIgnoringCase(String value, String message) {
    return new StringParser(value.length(), value::equalsIgnoreCase, message);
  }

  private final int size;
  private final Predicate<String> predicate;
  private final String message;

  private StringParser(int size, Predicate<String> predicate, String message) {
    this.size = size;
    this.predicate = Objects.requireNonNull(predicate, "Undefined predicate");
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  @Override
  public Result parseOn(Context context) {
    String buffer = context.getBuffer();
    int start = context.getPosition();
    int stop = start + size;
    if (stop <= buffer.length()) {
      String result = buffer.substring(start, stop);
      if (predicate.test(result)) {
        return context.success(result, stop);
      }
    }
    return context.failure(message);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    int stop = position + size;
    return stop <= buffer.length() &&
        predicate.test(buffer.substring(position, stop)) ? stop : -1;
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(size, ((StringParser) other).size) &&
        Objects.equals(predicate, ((StringParser) other).predicate) &&
        Objects.equals(message, ((StringParser) other).message);
  }

  @Override
  public StringParser copy() {
    return new StringParser(size, predicate, message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
