package org.petitparser;

import java.util.List;

import org.petitparser.context.Context;
import org.petitparser.parser.CharParser;
import org.petitparser.parser.Parser;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;

/**
 * Factory for character parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Chars {

  /**
   * Returns a parser that parses any character.
   */
  public static Parser any() {
    return any("character expected");
  }

  public static Parser any(String message) {
    return matcher(CharMatcher.ANY, message);
  }

  /**
   * Returns a parser that parses any of the provided characters.
   */
  public static Parser anyOf(String chars) {
    return anyOf(chars, "any of " + chars + " expected");
  }

  public static Parser anyOf(String chars, String message) {
    return matcher(CharMatcher.anyOf(chars), message);
  }

  /**
   * Returns a parser that parses a specific {@code character}.
   */
  public static Parser character(char character) {
    return character(character, character + " expected");
  }

  public static Parser character(char character, String message) {
    return matcher(CharMatcher.is(character), message);
  }

  /**
   * Returns a parser that parses a single digit.
   *
   * @see Character#isDigit(char)
   */
  public static Parser digit() {
    return digit("digit expected");
  }

  public static Parser digit(String message) {
    return matcher(CharMatcher.JAVA_DIGIT, message);
  }

  /**
   * Returns a parser that parses a single letter.
   *
   * @see Character#isLetter(char)
   */
  public static Parser letter() {
    return letter("letter expected");
  }

  public static Parser letter(String message) {
    return matcher(CharMatcher.JAVA_LETTER, message);
  }

  /**
   * Returns a parser that parses an lower-case letter.
   *
   * @see Character#isLowerCase(char)
   */
  public static Parser lowerCase() {
    return lowerCase("lowercase letter expected");
  }

  public static Parser lowerCase(String message) {
    return matcher(CharMatcher.JAVA_LOWER_CASE, message);
  }

  /**
   * Returns a parser that accepts the a specified matcher.
   *
   * @see CharMatcher
   */
  public static Parser matcher(CharMatcher matcher) {
    return matcher(matcher, matcher.toString());
  }

  public static Parser matcher(CharMatcher matcher, String message) {
    return new CharParser(matcher, message);
  }

  /**
   * Returns a parser that parses a specific character pattern.
   *
   * Characters match themselves. A dash {@code -} between two
   * characters matches the range of those characters. A caret
   * {@code ^} at the beginning negates the pattern.
   */
  public static Parser pattern(String pattern) {
    return pattern(pattern, pattern + " expected");
  }

  public static Parser pattern(String pattern, String message) {
    return matcher(patternPredicate(pattern), message);
  }

  private static CharMatcher patternPredicate(String pattern) {
    return PATTERN.parse(new Context(pattern)).get();
  }

  private static Parser PATTERN_SIMPLE = any().map(
      new Function<Character, CharMatcher>() {
        @Override
        public CharMatcher apply(Character character) {
          return CharMatcher.is(character);
        }
      });

  private static Parser PATTERN_RANGE = any().seq(character('-')).seq(any())
      .map(new Function<List<Character>, CharMatcher>() {
        @Override
        public CharMatcher apply(List<Character> characters) {
          return CharMatcher.inRange(characters.get(0), characters.get(2));
        }
      });

  private static Parser PATTERN_POSITIVE = PATTERN_RANGE.or(PATTERN_SIMPLE).plus()
      .map(new Function<List<CharMatcher>, CharMatcher>() {
        @Override
        public CharMatcher apply(List<CharMatcher> matchers) {
          CharMatcher result = matchers.remove(0);
          for (CharMatcher matcher : matchers) {
            result = result.or(matcher);
          }
          return result;
        }
      });

  private static Parser PATTERN = character('^').optional().seq(PATTERN_POSITIVE)
      .map(new Function<List<CharMatcher>, CharMatcher>() {
        @Override
        public CharMatcher apply(List<CharMatcher> matchers) {
          return matchers.get(0) == null ? matchers.get(1) : matchers.get(1).negate();
        }
      }).end();

  /**
   * Returns a parser that parses a specific character range.
   */
  public static Parser range(char start, char stop) {
    return range(start, stop, start + ".." + stop + " expected");
  }

  public static Parser range(char start, char stop, String message) {
    return matcher(CharMatcher.inRange(start, stop), message);
  }

  /**
   * Returns a parser that parses an upper-case letter.
   *
   * @see Character#isUpperCase(char)
   */
  public static Parser upperCase() {
    return upperCase("uppercase letter expected");
  }

  public static Parser upperCase(String message) {
    return matcher(CharMatcher.JAVA_UPPER_CASE, message);
  }

  /**
   * Returns a parser that parses a single whitespace.
   *
   * @see Character#isWhitespace(char)
   */
  public static Parser whitespace() {
    return whitespace("whitespace expected");
  }

  public static Parser whitespace(String message) {
    return matcher(CharMatcher.WHITESPACE, message);
  }

  /**
   * Returns a parser that parses a single letter or digit.
   *
   * @see Character#isLetterOrDigit(char)
   */
  public static Parser word() {
    return word("letter or digit expected");
  }

  public static Parser word(String message) {
    return matcher(CharMatcher.JAVA_LETTER_OR_DIGIT, message);
  }

}
