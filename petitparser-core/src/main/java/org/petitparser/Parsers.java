package org.petitparser;

import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.EpsilonParser;
import org.petitparser.parser.primitive.FailureParser;
import org.petitparser.parser.primitive.StringParser;

/**
 * Deprecated factory for generic parsers.
 */
@Deprecated
public class Parsers {

  private Parsers() { }

  /**
   * Replace this deprecated call with {@link EpsilonParser}.
   */
  @Deprecated
  public static Parser epsilon() {
    return new EpsilonParser();
  }

  /**
   * Replace this deprecated call with {@link FailureParser#withMessage(String)}.
   */
  @Deprecated
  public static Parser failure(String message) {
    return FailureParser.withMessage(message);
  }

  /**
   * Replace this deprecated call with {@link StringParser#of(String)}.
   */
  @Deprecated
  public static Parser string(String string) {
    return StringParser.of(string);
  }

  /**
   * Replace this deprecated call with {@link StringParser#of(String, String)}.
   */
  @Deprecated
  public static Parser string(String string, String message) {
    return StringParser.of(string, message);
  }

  /**
   * Replace this deprecated call with {@link StringParser#ofIgnoringCase(String)}.
   */
  @Deprecated
  public static Parser stringIgnoreCase(String string) {
    return StringParser.ofIgnoringCase(string);
  }

  /**
   * Replace this deprecated call with {@link StringParser#ofIgnoringCase(String, String)}.
   */
  @Deprecated
  public static Parser stringIgnoreCase(String string, String message) {
    return StringParser.ofIgnoringCase(string, message);
  }
}
