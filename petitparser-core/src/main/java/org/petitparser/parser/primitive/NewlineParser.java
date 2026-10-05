package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * A parser that consumes newlines platform independently.
 */
public class NewlineParser extends Parser {

  protected final String message;

  /**
   * Constructs a newline parser with a default failure message.
   */
  public NewlineParser() {
    this("newline expected");
  }

  /**
   * Constructs a newline parser with a custom failure message.
   *
   * @param message the failure message.
   */
  public NewlineParser(String message) {
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  /**
   * Returns the failure message.
   */
  public String getMessage() {
    return message;
  }

  @Override
  public Result parseOn(Context context) {
    String buffer = context.getBuffer();
    int position = context.getPosition();
    if (position < buffer.length()) {
      switch (buffer.charAt(position)) {
        case '\n':
          return context.success("\n", position + 1);
        case '\r':
          if (position + 1 < buffer.length() && buffer.charAt(position + 1) == '\n') {
            return context.success("\r\n", position + 2);
          } else {
            return context.success("\r", position + 1);
          }
      }
    }
    return context.failure(message);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    if (position < buffer.length()) {
      switch (buffer.charAt(position)) {
        case '\n':
          return position + 1;
        case '\r':
          return position + 1 < buffer.length() && buffer.charAt(position + 1) == '\n'
              ? position + 2
              : position + 1;
      }
    }
    return -1;
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }

  @Override
  public NewlineParser copy() {
    return new NewlineParser(message);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(message, ((NewlineParser) other).message);
  }
}
