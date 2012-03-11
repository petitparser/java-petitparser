package org.petitparser;

import org.petitparser.parser.AbstractParser;
import org.petitparser.parser.CharPredicateParser;
import org.petitparser.parser.CharPredicateParser.CharPredicate;
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
  public static AbstractParser<Character> any() {
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
  public static AbstractParser<Character> character(final char character) {
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
  public static AbstractParser<Character> digit() {
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
  public static AbstractParser<Character> letter() {
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
  public static AbstractParser<Character> word() {
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
  public static AbstractParser<Character> lowerCase() {
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
  public static AbstractParser<Character> upperCase() {
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
  public static AbstractParser<Character> whitespace() {
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
  public static AbstractParser<String> string(final String string) {
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
  public static AbstractParser<String> stringIgnoreCase(final String string) {
    return new StringPredicateParser(string.length(), new StringPredicate() {
      @Override
      public boolean apply(String argument) {
        return string.equalsIgnoreCase(argument);
      }
    }, string + " expected");
  }

}