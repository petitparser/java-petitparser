package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * Parses a single character.
 */
public class CharacterParser extends Parser {

  /**
   * Returns a parser that accepts a specific {@link CharacterPredicate}.
   */
  public static CharacterParser of(CharacterPredicate predicate, String message) {
    return new CharacterParser(predicate, message);
  }

  /**
   * Returns a parser that accepts a specific {@code character}.
   */
  public static CharacterParser of(char character) {
    return of(character, "'" + character + "' expected");
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
  public static CharacterParser anyOf(String chars) {
    return anyOf(chars, "any of '" + chars + "' expected");
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
  public static CharacterParser noneOf(String chars) {
    return noneOf(chars, "none of '" + chars + "' expected");
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
    return new CharacterParser(Character::isDigit, message);
  }

  /**
   * Returns a parser that accepts a single letter.
   */
  public static CharacterParser letter() {
    return letter("letter expected");
  }

  public static CharacterParser letter(String message) {
    return of(Character::isLetter, message);
  }

  /**
   * Returns a parser that accepts an lower-case letter.
   */
  public static CharacterParser lowerCase() {
    return lowerCase("lowercase letter expected");
  }

  public static CharacterParser lowerCase(String message) {
    return of(Character::isLowerCase, message);
  }

  /**
   * Returns a parser that accepts a specific character pattern.
   * <p>
   * Characters match themselves. A dash {@code -} between two characters matches the range of those
   * characters. A caret {@code ^} at the beginning negates the pattern.
   */
  public static CharacterParser pattern(String pattern) {
    return pattern(pattern, "[" + pattern + "] expected");
  }

  public static CharacterParser pattern(String pattern, String message) {
    return of(CharacterPredicate.pattern(pattern), message);
  }

  /**
   * Returns a parser that accepts a specific character range.
   */
  public static CharacterParser range(char start, char stop) {
    return range(start, stop, start + ".." + stop + " expected");
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
    return of(Character::isUpperCase, message);
  }

  /**
   * Returns a parser that accepts a single whitespace.
   */
  public static CharacterParser whitespace() {
    return whitespace("whitespace expected");
  }

  public static CharacterParser whitespace(String message) {
    return of(Character::isWhitespace, message);
  }

  /**
   * Returns a parser that accepts a single letter or digit.
   */
  public static CharacterParser word() {
    return word("letter or digit expected");
  }

  public static CharacterParser word(String message) {
    return of(Character::isLetterOrDigit, message);
  }

  private final CharacterPredicate matcher;
  private final String message;

  private CharacterParser(CharacterPredicate matcher, String message) {
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
  public CharacterParser neg(String message) {
    // Return an optimized version of the receiver.
    return of(matcher.not(), message);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(matcher, ((CharacterParser) other).matcher) &&
        Objects.equals(message, ((CharacterParser) other).message);
  }

  @Override
  public CharacterParser copy() {
    return of(matcher, message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
