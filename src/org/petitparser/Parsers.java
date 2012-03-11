package org.petitparser;

import org.petitparser.parser.CharPredicateParser;
import org.petitparser.parser.CharPredicateParser.CharPredicate;
import org.petitparser.parser.EpsilonParser;
import org.petitparser.parser.FailureParser;
import org.petitparser.parser.Parser;
import org.petitparser.parser.StringPredicateParser;
import org.petitparser.parser.StringPredicateParser.StringPredicate;

/**
 * Factory for common types of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Parsers {

  /**
   * Returns a parser that parses any character.
   */
  public static Parser any() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return true;
      }
    }, "input expected");
  }

  /**
   * Returns a parser that parses a specific {@code character}.
   */
  public static Parser character(final char character) {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return character == input;
      }
    }, character + " expected");
  }

  /**
   * Returns a parser that parses a single digit.
   */
  public static Parser digit() {
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
  public static Parser letter() {
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
  public static Parser word() {
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
  public static Parser lowerCase() {
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
  public static Parser upperCase() {
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
  public static Parser whitespace() {
    return new CharPredicateParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isWhitespace(input);
      }
    }, "whitespace expected");
  }

  /**
   * Returns a parser that parsers a specific string.
   */
  public static Parser string(final String string) {
    return new StringPredicateParser(string.length(), new StringPredicate() {
      @Override
      public boolean apply(String argument) {
        return string.equals(argument);
      }
    }, string + " expected");
  }

  /**
   * Returns a parser that parsers a specific string case-insensitive.
   */
  public static Parser stringIgnoreCase(final String string) {
    return new StringPredicateParser(string.length(), new StringPredicate() {
      @Override
      public boolean apply(String argument) {
        return string.equalsIgnoreCase(argument);
      }
    }, string + " expected");
  }

  /**
   * Returns a parser that always succeeds and never consumes anything.
   */
  public static Parser epsilon() {
    return new EpsilonParser();
  }

  /**
   * Returns a parser that always fails and never consumes anything.
   */
  public static Parser failure(String message) {
    return new FailureParser(message);
  }

}
