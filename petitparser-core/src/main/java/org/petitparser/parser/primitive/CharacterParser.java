package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import org.petitparser.parser.repeating.RepeatingCharacterParser;

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
    return new CharacterParser(predicate, message);
  }

  /**
   * Returns a parser that accepts a specific {@code character}.
   */
  public static CharacterParser of(char character) {
    return of(character, "'" + toReadableString(character) + "' expected");
  }

  public static CharacterParser of(char character, String message) {
    return of(CharacterPredicate.of(character), message);
  }

  /**
   * Returns a parser that accepts any character.
   */
  public static CharacterParser any() {
    return any("any character expected");
  }

  public static CharacterParser any(String message) {
    return of(CharacterPredicate.any(), message);
  }

  /**
   * Returns a parser that accepts any of the provided characters.
   */
  public static CharacterParser anyOf(String characters) {
    return anyOf(characters, "any of '" + toReadableString(characters) + "' " +
        "expected");
  }

  public static CharacterParser anyOf(String chars, String message) {
    return of(CharacterPredicate.anyOf(chars), message);
  }

  /**
   * Returns a parser that accepts no character.
   */
  public static CharacterParser none() {
    return none("no character expected");
  }

  public static CharacterParser none(String message) {
    return of(CharacterPredicate.none(), message);
  }

  /**
   * Returns a parser that accepts none of the provided characters.
   */
  public static CharacterParser noneOf(String characters) {
    return noneOf(characters, "none of '" + toReadableString(characters) + "'" +
        " expected");
  }

  public static CharacterParser noneOf(String chars, String message) {
    return of(CharacterPredicate.noneOf(chars), message);
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
    return pattern(pattern, "[" + toReadableString(pattern) + "] expected");
  }

  public static CharacterParser pattern(String pattern, String message) {
    return of(CharacterPredicate.pattern(pattern), message);
  }

  /**
   * Returns a parser that accepts a specific character range.
   */
  public static CharacterParser range(char start, char stop) {
    return range(start, stop,
        toReadableString(start) + ".." + toReadableString(stop) + " expected");
  }

  public static CharacterParser range(char start, char stop, String message) {
    return of(CharacterPredicate.range(start, stop), message);
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

  private static String toReadableString(String characters) {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < characters.length(); i++) {
      buffer.append(toReadableString(characters.charAt(i)));
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
