package org.petitparser;

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

  /**
   * Returns a parser that parsers a specific string.
   */
  public static Parser string(String string) {
    return string(string, string + " expected");
  }

  public static Parser string(final String string, String message) {
    return new StringPredicateParser(string.length(), new StringPredicate() {
      @Override
      public boolean apply(String argument) {
        return string.equals(argument);
      }
    }, message);
  }

  /**
   * Returns a parser that parsers a specific string case-insensitive.
   */
  public static Parser stringIgnoreCase(String string) {
    return stringIgnoreCase(string, string + " expected");
  }

  public static Parser stringIgnoreCase(final String string, String message) {
    return new StringPredicateParser(string.length(), new StringPredicate() {
      @Override
      public boolean apply(String argument) {
        return string.equalsIgnoreCase(argument);
      }
    }, message);
  }

}
