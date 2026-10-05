package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.repeating.RepeatingCharacterParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Parses a single character.
 */
public class CharacterParser extends Parser {

  /**
   * Returns a parser that accepts a specific {@link CharacterPredicate}.
   */
  public static CharacterParser of(
      CharacterPredicate predicate, String message) {
    return of(predicate, message, false);
  }

  public static CharacterParser of(
      CharacterPredicate predicate, String message, boolean unicode) {
    return unicode ? new UnicodeCharacterParser(predicate, message)
        : new CharacterParser(predicate, message);
  }

  /**
   * Returns a parser that accepts a specific {@code character}.
   */
  public static CharacterParser of(char character) {
    return of(character, false);
  }

  public static CharacterParser of(char character, boolean unicode) {
    return of(character, "'" + toReadableString(character) + "' expected", unicode);
  }

  public static CharacterParser of(char character, String message) {
    return of(character, message, false);
  }

  public static CharacterParser of(char character, String message, boolean unicode) {
    return of(new SingleCharPredicate(character), message, unicode);
  }

  public static CharacterParser of(int codePoint, boolean unicode) {
    return of(codePoint, "'" + toReadableString(codePoint) + "' expected", unicode);
  }

  public static CharacterParser of(int codePoint, String message, boolean unicode) {
    return of(new SingleCharPredicate(codePoint), message, unicode);
  }

  /**
   * Returns a parser that accepts any character.
   */
  public static CharacterParser any() {
    return any(false);
  }

  public static CharacterParser any(String message) {
    return any(message, false);
  }

  public static CharacterParser any(boolean unicode) {
    return any("any character expected", unicode);
  }

  public static CharacterParser any(String message, boolean unicode) {
    return of(ConstantCharPredicate.any(), message, unicode);
  }

  /**
   * Returns a parser that accepts any of the provided characters.
   */
  public static CharacterParser anyOf(String characters) {
    return anyOf(characters, false);
  }

  public static CharacterParser anyOf(String characters, boolean unicode) {
    return anyOf(characters, "any of '" + toReadableString(characters) + "' expected", unicode);
  }

  public static CharacterParser anyOf(String chars, String message) {
    return anyOf(chars, message, false);
  }

  public static CharacterParser anyOf(String chars, String message, boolean unicode) {
    return of(CharacterPredicate.optimizedString(chars, false, unicode), message, unicode);
  }

  /**
   * Returns a parser that accepts no character.
   */
  public static CharacterParser none() {
    return none(false);
  }

  public static CharacterParser none(String message) {
    return none(message, false);
  }

  public static CharacterParser none(boolean unicode) {
    return none("no character expected", unicode);
  }

  public static CharacterParser none(String message, boolean unicode) {
    return of(ConstantCharPredicate.none(), message, unicode);
  }

  /**
   * Returns a parser that accepts none of the provided characters.
   */
  public static CharacterParser noneOf(String characters) {
    return noneOf(characters, false);
  }

  public static CharacterParser noneOf(String characters, boolean unicode) {
    return noneOf(characters, "none of '" + toReadableString(characters) + "' expected", unicode);
  }

  public static CharacterParser noneOf(String chars, String message) {
    return noneOf(chars, message, false);
  }

  public static CharacterParser noneOf(String chars, String message, boolean unicode) {
    return of(CharacterPredicate.optimizedString(chars, false, unicode).not(), message, unicode);
  }

  /**
   * Returns a parser that accepts a single digit.
   */
  public static CharacterParser digit() {
    return digit("digit expected");
  }

  public static CharacterParser digit(String message) {
    return of(DigitCharPredicate.INSTANCE, message);
  }

  /**
   * Returns a parser that accepts a single letter.
   */
  public static CharacterParser letter() {
    return letter("letter expected");
  }

  public static CharacterParser letter(String message) {
    return of(LetterCharPredicate.INSTANCE, message);
  }

  /**
   * Returns a parser that accepts an lower-case letter.
   */
  public static CharacterParser lowerCase() {
    return lowerCase("lowercase letter expected");
  }

  public static CharacterParser lowerCase(String message) {
    return of(LowercaseCharPredicate.INSTANCE, message);
  }

  /**
   * Returns a parser that accepts a specific character pattern.
   *
   * <p>Characters match themselves. A dash {@code -} between two characters
   * matches the range of those characters. A caret {@code ^} at the
   * beginning negates the pattern.
   */
  public static CharacterParser pattern(String pattern) {
    return pattern(pattern, false);
  }

  public static CharacterParser pattern(String pattern, String message) {
    return pattern(pattern, message, false, false);
  }

  public static CharacterParser pattern(String pattern, boolean unicode) {
    return pattern(pattern, null, false, unicode);
  }

  public static CharacterParser pattern(String pattern, String message, boolean unicode) {
    return pattern(pattern, message, false, unicode);
  }

  @SuppressWarnings("unchecked")
  public static CharacterParser pattern(
      String pattern, String message, boolean ignoreCase, boolean unicode) {
    String input = pattern;
    boolean isNegated = input.startsWith("^");
    if (isNegated) {
      input = input.substring(1);
    }
    Parser parser = unicode
        ? PatternGrammar.PATTERN_UNICODE_PARSER
        : PatternGrammar.PATTERN_CHAR_PARSER;
    List<RangeCharPredicate> parsedRanges = (List<RangeCharPredicate>) parser.parse(input).get();
    List<RangeCharPredicate> ranges = ignoreCase
        ? expandCase(parsedRanges, unicode)
        : parsedRanges;
    CharacterPredicate predicate = CharacterPredicate.optimizedRanges(ranges, unicode);
    if (isNegated) {
      predicate = predicate.not();
    }
    if (message == null) {
      message = "[" + toReadableString(pattern) + "]" +
          (ignoreCase ? " (case-insensitive)" : "") + " expected";
    }
    return of(predicate, message, unicode);
  }

  private static List<RangeCharPredicate> expandCase(
      List<RangeCharPredicate> ranges, boolean unicode) {
    List<RangeCharPredicate> result = new ArrayList<>();
    for (RangeCharPredicate range : ranges) {
      result.add(range);
      if (range.getStart() <= 0 && range.getStop() >= (unicode ? 0x10ffff : 0xffff)) {
        continue;
      }
      for (int code = range.getStart(); code <= range.getStop(); code++) {
        if (unicode) {
          String str = new String(Character.toChars(code));
          String lower = str.toLowerCase();
          String upper = str.toUpperCase();
          if (!lower.equals(str) && lower.codePointCount(0, lower.length()) == 1) {
            int cp = lower.codePointAt(0);
            result.add(new RangeCharPredicate(cp, cp));
          }
          if (!upper.equals(str) && upper.codePointCount(0, upper.length()) == 1) {
            int cp = upper.codePointAt(0);
            result.add(new RangeCharPredicate(cp, cp));
          }
        } else {
          char c = (char) code;
          char lower = Character.toLowerCase(c);
          char upper = Character.toUpperCase(c);
          if (lower != c) {
            result.add(new RangeCharPredicate(lower, lower));
          }
          if (upper != c) {
            result.add(new RangeCharPredicate(upper, upper));
          }
        }
      }
    }
    return result;
  }

  private static class PatternGrammar {
    static final Parser PATTERN_CHAR_PARSER = createPatternParser(false);
    static final Parser PATTERN_UNICODE_PARSER = createPatternParser(true);

    private static Parser createPatternParser(boolean unicode) {
      Parser character = unicode ? any(true) : any(false);
      Parser single = character.map(val -> {
        int cp = (val instanceof Character)
            ? (Character) val
            : ((String) val).codePointAt(0);
        return new RangeCharPredicate(cp, cp);
      });
      Parser range = character.seq(of('-')).seq(character).map((List<Object> values) -> {
        int start = (values.get(0) instanceof Character)
            ? (Character) values.get(0)
            : ((String) values.get(0)).codePointAt(0);
        int stop = (values.get(2) instanceof Character)
            ? (Character) values.get(2)
            : ((String) values.get(2)).codePointAt(0);
        return new RangeCharPredicate(start, stop);
      });
      return range.or(single).star().end();
    }
  }

  /**
   * Returns a parser that accepts a specific character range.
   */
  public static CharacterParser range(char start, char stop) {
    return range(start, stop, false);
  }

  public static CharacterParser range(char start, char stop, boolean unicode) {
    return range((int) start, (int) stop, unicode);
  }

  public static CharacterParser range(char start, char stop, String message) {
    return range((int) start, (int) stop, message, false);
  }

  public static CharacterParser range(char start, char stop, String message, boolean unicode) {
    return range((int) start, (int) stop, message, unicode);
  }

  public static CharacterParser range(int start, int stop, boolean unicode) {
    return range(start, stop,
        toReadableString(start) + ".." + toReadableString(stop) + " expected", unicode);
  }

  public static CharacterParser range(int start, int stop, String message, boolean unicode) {
    return of(new RangeCharPredicate(start, stop), message, unicode);
  }

  /**
   * Returns a parser that accepts an upper-case letter.
   */
  public static CharacterParser upperCase() {
    return upperCase("uppercase letter expected");
  }

  public static CharacterParser upperCase(String message) {
    return of(UppercaseCharPredicate.INSTANCE, message);
  }

  /**
   * Returns a parser that accepts a single whitespace.
   */
  public static CharacterParser whitespace() {
    return whitespace("whitespace expected");
  }

  public static CharacterParser whitespace(String message) {
    return of(WhitespaceCharPredicate.INSTANCE, message);
  }

  /**
   * Returns a parser that accepts a single letter or digit.
   */
  public static CharacterParser word() {
    return word("letter or digit expected");
  }

  public static CharacterParser word(String message) {
    return of(WordCharPredicate.INSTANCE, message);
  }

  private final CharacterPredicate matcher;
  private final String message;

  protected CharacterParser(CharacterPredicate matcher, String message) {
    this.matcher = Objects.requireNonNull(matcher, "Undefined matcher");
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  @Override
  public Result parseOn(Context context) {
    String buffer = context.getBuffer();
    int position = context.getPosition();
    if (position < buffer.length()) {
      char result = buffer.charAt(position);
      if (matcher.test(result)) {
        return context.success(result, position + 1);
      }
    }
    return context.failure(message);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return position < buffer.length() && matcher.test(buffer.charAt(position)) ?
        position + 1 : -1;
  }

  public CharacterPredicate getMatcher() {
    return matcher;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public RepeatingCharacterParser starString() {
    return repeatString(0, org.petitparser.parser.repeating.RepeatingParser.UNBOUNDED);
  }

  @Override
  public RepeatingCharacterParser starString(String message) {
    return repeatString(0, org.petitparser.parser.repeating.RepeatingParser.UNBOUNDED, message);
  }

  @Override
  public RepeatingCharacterParser plusString() {
    return repeatString(1, org.petitparser.parser.repeating.RepeatingParser.UNBOUNDED);
  }

  @Override
  public RepeatingCharacterParser plusString(String message) {
    return repeatString(1, org.petitparser.parser.repeating.RepeatingParser.UNBOUNDED, message);
  }

  @Override
  public RepeatingCharacterParser timesString(int count) {
    return repeatString(count, count);
  }

  @Override
  public RepeatingCharacterParser timesString(int count, String message) {
    return repeatString(count, count, message);
  }

  @Override
  public RepeatingCharacterParser repeatString(int min, int max) {
    return repeatString(min, max, null);
  }

  @Override
  public RepeatingCharacterParser repeatString(int min, int max, String message) {
    return new RepeatingCharacterParser(
        matcher, message != null ? message : this.message, min, max);
  }

  @Override
  public CharacterParser neg(String message) {
    // Return an optimized version of the receiver.
    return of(matcher.not(), message);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    CharacterParser that = (CharacterParser) other;
    return super.hasEqualProperties(other) &&
        (matcher == that.matcher || matcher.isEqualTo(that.matcher)) &&
        Objects.equals(message, that.message);
  }

  @Override
  public CharacterParser copy() {
    return of(matcher, message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }

  public static String toReadableString(int codePoint) {
    if (codePoint <= Character.MAX_VALUE) {
      return toReadableString((char) codePoint);
    }
    return new String(Character.toChars(codePoint));
  }

  public static String toReadableString(String characters) {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < characters.length(); ) {
      int cp = characters.codePointAt(i);
      buffer.append(toReadableString(cp));
      i += Character.charCount(cp);
    }
    return buffer.toString();
  }

  private static String toReadableString(char character) {
    switch (character) {
      case '\b':
        return "\\b";  // backspace
      case '\t':
        return "\\t";  // horizontal tab
      case '\n':
        return "\\n";  // new line
      case '\f':
        return "\\f";  // form feed
      case '\r':
        return "\\r";  // carriage return
    }
    if (Character.isISOControl(character)) {
      String escape = Integer.toHexString(character);
      while (escape.length() < 4) {
        escape = "0" + escape;
      }
      return "\\u" + escape;
    }
    return Character.toString(character);
  }
}
