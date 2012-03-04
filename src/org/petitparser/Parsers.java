package org.petitparser;

import org.petitparser.parser.CharPredicateParser;
import org.petitparser.parser.CharPredicateParser.CharPredicate;

/**
 * Factory for common types of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Parsers {

  /**
   * Returns a parser that parses a specific {@code character}.
   */
  public static CharPredicateParser character(final char character) {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return character == input;
      }
    }, character + " expected");
  }

  /**
   * Returns a parser that parses any character.
   */
  public static CharPredicateParser any() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return true;
      }
    }, "input expected");
  }

  /**
   * Returns a parser that parses a single digit.
   */
  public static CharPredicateParser digit() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isDigit(input);
      }
    }, "digit expected");
  }

  /**
   * Returns a parser that parses a single letter.
   */
  public static CharPredicateParser letter() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLetter(input);
      }
    }, "letter expected");
  }

  /**
   * Returns a parser that parses a single letter or digit.
   */
  public static CharPredicateParser word() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLetterOrDigit(input);
      }
    }, "letter or digit expected");
  }

  /**
   * Returns a parser that parses an lower-case letter.
   */
  public static CharPredicateParser lowerCase() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLowerCase(input);
      }
    }, "lowercase letter expected");
  }

  /**
   * Returns a parser that parses an upper-case letter.
   */
  public static CharPredicateParser upperCase() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isUpperCase(input);
      }
    }, "uppercase letter expected");
  }

  /**
   * Returns a parser that parses a single whitespace.
   */
  public static CharPredicateParser whitespace() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isWhitespace(input);
      }
    }, "whitespace expected");
  }

}