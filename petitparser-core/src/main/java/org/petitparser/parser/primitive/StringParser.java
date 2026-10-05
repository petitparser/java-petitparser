package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * Parses a sequence of characters.
 */
public class StringParser extends Parser {

  /**
   * Construct a parser that accepts the provided {@link String} {@code value}.
   */
  public static Parser of(String value) {
    return of(value, value + " expected");
  }

  /**
   * Construct a parser that accepts the provided {@link String} {@code value},
   * and that fails with the provided error {@code message}.
   */
  public static Parser of(String value, String message) {
    return new StringParser(value, message);
  }

  /**
   * Construct a parser that accepts the provided {@link String} {@code value}
   * case insensitive.
   */
  public static Parser ofIgnoringCase(String value) {
    return StringIgnoreCaseParser.of(value);
  }

  /**
   * Construct a parser that accepts the provided {@link String} {@code value}
   * case insensitive, and that fails with the provided error {@code message}.
   */
  public static Parser ofIgnoringCase(String value, String message) {
    return StringIgnoreCaseParser.of(value, message);
  }

  private final String value;
  private final String message;

  public StringParser(String value, String message) {
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
    if (buffer.startsWith(value, start)) {
      return context.success(value, start + value.length());
    }
    return context.failure(message);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return buffer.startsWith(value, position) ? position + value.length() : -1;
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    StringParser that = (StringParser) other;
    return super.hasEqualProperties(other) &&
        Objects.equals(value, that.value) &&
        Objects.equals(message, that.message);
  }

  @Override
  public StringParser copy() {
    return new StringParser(value, message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
