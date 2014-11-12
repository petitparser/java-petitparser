package org.petitparser.parser.characters;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.List;
import java.util.Objects;

/**
 * Parses a single character.
 */
public class CharacterParser extends Parser {

  /**
   * Returns a parser that accepts any character.
   */
  public static Parser any() {
    return any("any character expected");
  }

  public static Parser any(String message) {
    return new CharacterParser(CharacterPredicate.any(), message);
  }

  /**
   * Returns a parser that accepts any of the provided characters.
   */
  public static Parser anyOf(String chars) {
    return anyOf(chars, "any of '" + chars + "' expected");
  }

  public static Parser anyOf(String chars, String message) {
    return new CharacterParser(CharacterPredicate.anyOf(chars), message);
  }

  /**
   * Returns a parser that accepts no character.
   */
  public static Parser none() {
    return none("no character expected");
  }

  public static Parser none(String message) {
    return new CharacterParser(CharacterPredicate.none(), message);
  }

  /**
   * Returns a parser that accepts none of the provided characters.
   */
  public static Parser noneOf(String chars) {
    return noneOf(chars, "none of '" + chars + "' expected");
  }

  public static Parser noneOf(String chars, String message) {
    return new CharacterParser(CharacterPredicate.noneOf(chars), message);
  }

  /**
   * Returns a parser that accepts a specific {@code character}.
   */
  public static Parser is(char character) {
    return is(character, "'" + character + "' expected");
  }

  public static Parser is(char character, String message) {
    return new CharacterParser(CharacterPredicate.is(character), message);
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
    return new CharacterParser(Character::isLetter, message);
  }

  /**
   * Returns a parser that accepts an lower-case letter.
   */
  public static Parser lowerCase() {
    return lowerCase("lowercase letter expected");
  }

  public static Parser lowerCase(String message) {
    return new CharacterParser(Character::isLowerCase, message);
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
    return new CharacterParser(PATTERN.parse(pattern).get(), message);
  }

  private static final Parser PATTERN_SIMPLE = any().map(CharacterPredicate::is);
  private static final Parser PATTERN_RANGE = any().seq(is('-')).seq(any())
      .map((List<Character> characters) -> {
        return CharacterPredicate.range(characters.get(0), characters.get(2));
      });
  private static final Parser PATTERN_POSITIVE = PATTERN_RANGE.or(PATTERN_SIMPLE).plus()
      .map((List<CharacterPredicate> matchers) -> {
        CharacterPredicate result = matchers.remove(0);
        for (CharacterPredicate matcher : matchers) {
          result = result.or(matcher);
        }
        return result;
      });
  private static final Parser PATTERN = is('^').optional().seq(PATTERN_POSITIVE)
      .map((List<CharacterPredicate> matchers) -> {
        return matchers.get(0) == null ? matchers.get(1) : matchers.get(1).not();
      }).end();

  /**
   * Returns a parser that accepts a specific character range.
   */
  public static Parser range(char start, char stop) {
    return range(start, stop, start + ".." + stop + " expected");
  }

  public static Parser range(char start, char stop, String message) {
    return new CharacterParser(CharacterPredicate.range(start, stop), message);
  }

  /**
   * Returns a parser that accepts an upper-case letter.
   */
  public static Parser upperCase() {
    return upperCase("uppercase letter expected");
  }

  public static Parser upperCase(String message) {
    return new CharacterParser(Character::isUpperCase, message);
  }

  /**
   * Returns a parser that accepts a single whitespace.
   */
  public static Parser whitespace() {
    return whitespace("whitespace expected");
  }

  public static Parser whitespace(String message) {
    return new CharacterParser(Character::isWhitespace, message);
  }

  /**
   * Returns a parser that accepts a single letter or digit.
   */
  public static Parser word() {
    return word("letter or digit expected");
  }

  public static Parser word(String message) {
    return new CharacterParser(Character::isLetterOrDigit, message);
  }

  private final CharacterPredicate matcher;
  private final String message;

  public CharacterParser(CharacterPredicate matcher, String message) {
    this.matcher = matcher;
    this.message = message;
  }

  @Override
  public Result parseOn(Context context) {
    String buffer = context.getBuffer();
    if (context.getPosition() < buffer.length()) {
      char result = buffer.charAt(context.getPosition());
      if (matcher.test(result)) {
        return context.success(result, context.getPosition() + 1);
      }
    }
    return context.failure(message);
  }

  @Override
  public Parser neg(String message) {
    return new CharacterParser(matcher.not(), message);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(matcher, ((CharacterParser) other).matcher) &&
        Objects.equals(message, ((CharacterParser) other).message);
  }

  @Override
  public Parser copy() {
    return new CharacterParser(matcher, message);
  }
}
