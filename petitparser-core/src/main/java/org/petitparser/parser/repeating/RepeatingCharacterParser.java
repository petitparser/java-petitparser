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
  protected final boolean unicode;

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
    this(predicate, message, min, max, false);
  }

  /**
   * Constructs a repeating character parser with Unicode code point support.
   */
  public RepeatingCharacterParser(
      CharacterPredicate predicate, String message, int min, int max, boolean unicode) {
    this.predicate = Objects.requireNonNull(predicate, "Undefined predicate");
    this.message = Objects.requireNonNull(message, "Undefined message");
    this.min = min;
    this.max = max;
    this.unicode = unicode;
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

  public boolean isUnicode() {
    return unicode;
  }

  @Override
  public Result parseOn(Context context) {
    String buffer = context.getBuffer();
    int position = context.getPosition();
    int end = buffer.length();
    int current = position;
    int count = 0;
    if (unicode) {
      while (count < min) {
        if (current >= end) {
          return context.failure(message, current);
        }
        char ch = buffer.charAt(current);
        int next = current + 1;
        int codePoint = ch;
        if (Character.isHighSurrogate(ch) && next < end) {
          char nextCh = buffer.charAt(next);
          if (Character.isLowSurrogate(nextCh)) {
            codePoint = Character.toCodePoint(ch, nextCh);
            next++;
          }
        }
        if (!predicate.test(codePoint)) {
          return context.failure(message, current);
        }
        current = next;
        count++;
      }
      while (max == RepeatingParser.UNBOUNDED || count < max) {
        if (current >= end) {
          break;
        }
        char ch = buffer.charAt(current);
        int next = current + 1;
        int codePoint = ch;
        if (Character.isHighSurrogate(ch) && next < end) {
          char nextCh = buffer.charAt(next);
          if (Character.isLowSurrogate(nextCh)) {
            codePoint = Character.toCodePoint(ch, nextCh);
            next++;
          }
        }
        if (!predicate.test(codePoint)) {
          break;
        }
        current = next;
        count++;
      }
      return context.success(buffer.substring(position, current), current);
    } else {
      while (count < min) {
        if (current >= end || !predicate.test(buffer.charAt(current))) {
          return context.failure(message, current);
        }
        current++;
        count++;
      }
      while (max == RepeatingParser.UNBOUNDED || count < max) {
        if (current >= end || !predicate.test(buffer.charAt(current))) {
          break;
        }
        current++;
        count++;
      }
      return context.success(buffer.substring(position, current), current);
    }
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    int end = buffer.length();
    int current = position;
    int count = 0;
    if (unicode) {
      while (count < min) {
        if (current >= end) {
          return -1;
        }
        char ch = buffer.charAt(current);
        int next = current + 1;
        int codePoint = ch;
        if (Character.isHighSurrogate(ch) && next < end) {
          char nextCh = buffer.charAt(next);
          if (Character.isLowSurrogate(nextCh)) {
            codePoint = Character.toCodePoint(ch, nextCh);
            next++;
          }
        }
        if (!predicate.test(codePoint)) {
          return -1;
        }
        current = next;
        count++;
      }
      while (max == RepeatingParser.UNBOUNDED || count < max) {
        if (current >= end) {
          break;
        }
        char ch = buffer.charAt(current);
        int next = current + 1;
        int codePoint = ch;
        if (Character.isHighSurrogate(ch) && next < end) {
          char nextCh = buffer.charAt(next);
          if (Character.isLowSurrogate(nextCh)) {
            codePoint = Character.toCodePoint(ch, nextCh);
            next++;
          }
        }
        if (!predicate.test(codePoint)) {
          break;
        }
        current = next;
        count++;
      }
      return current;
    } else {
      while (count < min) {
        if (current >= end || !predicate.test(buffer.charAt(current))) {
          return -1;
        }
        current++;
        count++;
      }
      while (max == RepeatingParser.UNBOUNDED || count < max) {
        if (current >= end || !predicate.test(buffer.charAt(current))) {
          break;
        }
        current++;
        count++;
      }
      return current;
    }
  }

  @Override
  public RepeatingCharacterParser copy() {
    return new RepeatingCharacterParser(predicate, message, min, max, unicode);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    RepeatingCharacterParser that = (RepeatingCharacterParser) other;
    return super.hasEqualProperties(other) &&
        Objects.equals(predicate, that.predicate) &&
        Objects.equals(message, that.message) &&
        min == that.min &&
        max == that.max &&
        unicode == that.unicode;
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + ", " + getRange() + "]";
  }

  private String getRange() {
    return min + ".." + (max == RepeatingParser.UNBOUNDED ? "*" : max);
  }
}
