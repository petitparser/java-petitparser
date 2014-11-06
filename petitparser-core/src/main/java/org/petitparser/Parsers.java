package org.petitparser;

import static org.petitparser.Chars.character;

import org.petitparser.parser.EpsilonParser;
import org.petitparser.parser.FailureParser;
import org.petitparser.parser.Parser;
import org.petitparser.parser.StringPredicateParser;
import org.petitparser.parser.StringPredicateParser.StringPredicate;

/**
 * Factory for parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Parsers {

  /**
   * Returns a parser that always succeeds and never consumes anything.
   *
   * @see EpsilonParser
   */
  public static Parser epsilon() {
    return new EpsilonParser();
  }

  /**
   * Returns a parser that always fails and never consumes anything.
   *
   * @see FailureParser
   */
  public static Parser failure(String message) {
    return new FailureParser(message);
  }

  /**
   * Returns a parser that parsers a specific string.
   *
   * @see StringPredicateParser
   */
  public static Parser string(String string) {
    return string(string, string + " expected");
  }

  public static Parser string(final String string, String message) {
    if (string.length() == 1) {
      return character(string.charAt(0), message);
    } else {
      return new StringPredicateParser(string.length(), string::equals, message);
    }
  }

  /**
   * Returns a parser that parsers a specific string case-insensitive.
   *
   * @see StringPredicateParser
   */
  public static Parser stringIgnoreCase(String string) {
    return stringIgnoreCase(string, string + " expected");
  }

  public static Parser stringIgnoreCase(final String string, String message) {
    return new StringPredicateParser(string.length(), string::equalsIgnoreCase, message);
  }

}
