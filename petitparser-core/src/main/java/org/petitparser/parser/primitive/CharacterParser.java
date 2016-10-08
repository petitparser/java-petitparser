package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * Parses a single character.
 */
public class CharacterParser extends Parser {


  private final CharacterPredicate matcher;
  private final String message;

  private CharacterParser(CharacterPredicate matcher, String message) {
    this.matcher = Objects.requireNonNull(matcher, "Undefined matcher");
    this.message = Objects.requireNonNull(message, "Undefined message");
  }

  /**
   * Returns a parser that accepts a specific {@link CharacterPredicate}.
   */
  public static Parser of(CharacterPredicate predicate, String message) {
    return new CharacterParser(predicate, message);
  }

  /**
   * Returns a parser that accepts a specific {@code character}.
   */
  public static Parser of(char character) {
    return of(character, "'" + character + "' expected");
  }

  public static Parser of(char character, String message) {
    return of(CharacterPredicate.of(character), message);
  }

  /**
   * Returns a parser that accepts any character.
   */
  public static Parser any() {
    return any("any character expected");
  }

  public static Parser any(String message) {
    return of(CharacterPredicate.any(), message);
  }

  /**
   * Returns a parser that accepts any of the provided characters.
   */
  public static Parser anyOf(String chars) {
    return anyOf(chars, "any of '" + chars + "' expected");
  }

  public static Parser anyOf(String chars, String message) {
    return of(CharacterPredicate.anyOf(chars), message);
  }

  /**
   * Returns a parser that accepts no character.
   */
  public static Parser none() {
    return none("no character expected");
  }

  public static Parser none(String message) {
    return of(CharacterPredicate.none(), message);
  }

  /**
   * Returns a parser that accepts none of the provided characters.
   */
  public static Parser noneOf(String chars) {
    return noneOf(chars, "none of '" + chars + "' expected");
  }

  public static Parser noneOf(String chars, String message) {
    return of(CharacterPredicate.noneOf(chars), message);
  }

  /**
   * Returns a parser that accepts a single digit.
   */
  public static Parser digit() {
    return digit("digit expected");
  }

  public static Parser digit(String message) {
    return new CharacterParser(Character::isDigit, message);
  }

  /**
   * Returns a parser that accepts a single letter.
   */
  public static Parser letter() {
    return letter("letter expected");
  }

  public static Parser letter(String message) {
    return of(Character::isLetter, message);
  }

  /**
   * Returns a parser that accepts an lower-case letter.
   */
  public static Parser lowerCase() {
    return lowerCase("lowercase letter expected");
  }

  public static Parser lowerCase(String message) {
    return of(Character::isLowerCase, message);
  }

  /**
   * Returns a parser that accepts a specific character pattern.
   * <p>
   * Characters match themselves. A dash {@code -} between two characters matches the range of those
   * characters. A caret {@code ^} at the beginning negates the pattern.
   */
  public static Parser pattern(String pattern) {
    return pattern(pattern, "[" + pattern + "] expected");
  }

  public static Parser pattern(String pattern, String message) {
    return of(CharacterPredicate.pattern(pattern), message);
  }

  /**
   * Returns a parser that accepts a specific character range.
   */
  public static Parser range(char start, char stop) {
    return range(start, stop, start + ".." + stop + " expected");
  }

  public static Parser range(char start, char stop, String message) {
    return of(CharacterPredicate.range(start, stop), message);
  }

  /**
   * Returns a parser that accepts an upper-case letter.
   */
  public static Parser upperCase() {
    return upperCase("uppercase letter expected");
  }

  public static Parser upperCase(String message) {
    return of(Character::isUpperCase, message);
  }

  /**
   * Returns a parser that accepts a single whitespace.
   */
  public static Parser whitespace() {
    return whitespace("whitespace expected");
  }

  public static Parser whitespace(String message) {
    return of(Character::isWhitespace, message);
  }

  /**
   * Returns a parser that accepts a single letter or digit.
   */
  public static Parser word() {
    return word("letter or digit expected");
  }

  public static Parser word(String message) {
    return of(Character::isLetterOrDigit, message);
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
  public Parser neg(String message) {
    return of(matcher.not(), message);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(matcher, ((CharacterParser) other).matcher) &&
        Objects.equals(message, ((CharacterParser) other).message);
  }

  @Override
  public Parser copy() {
    return of(matcher, message);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + message + "]";
  }
}
