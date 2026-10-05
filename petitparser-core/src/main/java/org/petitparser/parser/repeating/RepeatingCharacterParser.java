package org.petitparser.parser.repeating;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterPredicate;

import java.util.Objects;

/**
 * A parser that repeatedly parses between 'min' and 'max' instances of a character predicate
 * and returns the consumed range as a {@link String}.
 */
public class RepeatingCharacterParser extends Parser {

  protected final CharacterPredicate predicate;
  protected final String message;
  protected final int min;
  protected final int max;

  /**
   * Constructs a repeating character parser.
   *
   * @param predicate the predicate matching each character.
   * @param message the failure message to report if min repetitions are not satisfied.
   * @param min the minimum number of repetitions.
   * @param max the maximum number of repetitions, or {@link RepeatingParser#UNBOUNDED}.
   */
  public RepeatingCharacterParser(
      CharacterPredicate predicate, String message, int min, int max) {
    this.predicate = Objects.requireNonNull(predicate, "Undefined predicate");
    this.message = Objects.requireNonNull(message, "Undefined message");
    this.min = min;
    this.max = max;
    if (min < 0) {
      throw new IllegalArgumentException("Invalid min repetitions: " + getRange());
    }
    if (max != RepeatingParser.UNBOUNDED && min > max) {
      throw new IllegalArgumentException("Invalid max repetitions: " + getRange());
    }
  }

  public CharacterPredicate getPredicate() {
    return predicate;
  }

  public String getMessage() {
    return message;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  @Override
  public Result parseOn(Context context) {
    String buffer = context.getBuffer();
    int position = context.getPosition();
    int current = position;
    int count = 0;
    while (count < min) {
      if (current >= buffer.length() || !predicate.test(buffer.charAt(current))) {
        return context.failure(message, current);
      }
      current++;
      count++;
    }
    while (max == RepeatingParser.UNBOUNDED || count < max) {
      if (current >= buffer.length() || !predicate.test(buffer.charAt(current))) {
        break;
      }
      current++;
      count++;
    }
    return context.success(buffer.substring(position, current), current);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    int current = position;
    int count = 0;
    while (count < min) {
      if (current >= buffer.length() || !predicate.test(buffer.charAt(current))) {
        return -1;
      }
      current++;
      count++;
    }
    while (max == RepeatingParser.UNBOUNDED || count < max) {
      if (current >= buffer.length() || !predicate.test(buffer.charAt(current))) {
        break;
      }
      current++;
      count++;
    }
    return current;
  }

  @Override
  public RepeatingCharacterParser copy() {
    return new RepeatingCharacterParser(predicate, message, min, max);
  }

  @Override
  public boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(predicate, ((RepeatingCharacterParser) other).predicate) &&
        Objects.equals(message, ((RepeatingCharacterParser) other).message) &&
        min == ((RepeatingCharacterParser) other).min &&
        max == ((RepeatingCharacterParser) other).max;
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + ", " + getRange() + "]";
  }

  private String getRange() {
    return min + ".." + (max == RepeatingParser.UNBOUNDED ? "*" : max);
  }
}
