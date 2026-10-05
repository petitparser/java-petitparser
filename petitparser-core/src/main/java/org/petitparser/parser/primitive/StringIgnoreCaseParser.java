package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * Parses a sequence of characters ignoring case.
 */
public class StringIgnoreCaseParser extends Parser {

  /**
   * Construct a parser that accepts the provided {@link String} {@code value}
   * case-insensitively.
   */
  public static StringIgnoreCaseParser of(String value) {
    return of(value, value + " expected");
  }

  /**
   * Construct a parser that accepts the provided {@link String} {@code value}
   * case-insensitively, failing with {@code message}.
   */
  public static StringIgnoreCaseParser of(String value, String message) {
    return new StringIgnoreCaseParser(value, message);
  }

  private final String value;
  private final String message;

  public StringIgnoreCaseParser(String value, String message) {
    this.value = Objects.requireNonNull(value, "Undefined value");
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  public String getValue() {
    return value;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public Result parseOn(Context context) {
    String buffer = context.getBuffer();
    int start = context.getPosition();
    int size = value.length();
    int stop = start + size;
    if (stop <= buffer.length() &&
        buffer.regionMatches(true, start, value, 0, size)) {
      return context.success(buffer.substring(start, stop), stop);
    }
    return context.failure(message);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    int size = value.length();
    return position + size <= buffer.length() &&
        buffer.regionMatches(true, position, value, 0, size) ? position + size : -1;
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    StringIgnoreCaseParser that = (StringIgnoreCaseParser) other;
    return super.hasEqualProperties(other) &&
        value.equalsIgnoreCase(that.value) &&
        Objects.equals(message, that.message);
  }

  @Override
  public StringIgnoreCaseParser copy() {
    return new StringIgnoreCaseParser(value, message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
