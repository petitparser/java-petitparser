package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * A parser that consumes nothing and always succeeds.
 */
public class EpsilonParser extends Parser {

  /**
   * Reusable singleton instance of {@link EpsilonParser} returning {@code null}.
   */
  public static final EpsilonParser INSTANCE = new EpsilonParser();

  protected final Object value;

  /**
   * Constructs an epsilon parser that produces {@code null}.
   */
  public EpsilonParser() {
    this(null);
  }

  /**
   * Constructs an epsilon parser that produces {@code value}.
   *
   * @param value the result value.
   */
  public EpsilonParser(Object value) {
    this.value = value;
  }

  /**
   * Returns the value produced on success.
   */
  public Object getValue() {
    return value;
  }

  @Override
  public Result parseOn(Context context) {
    return context.success(value);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return position;
  }

  @Override
  public EpsilonParser copy() {
    return new EpsilonParser(value);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(value, ((EpsilonParser) other).value);
  }

  @Override
  public String toString() {
    return value == null ? super.toString() : super.toString() + "[" + value + "]";
  }
}
