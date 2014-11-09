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

  private Parsers() {
  }

  @Deprecated
  public static Parser epsilon() {
    return EpsilonParser.DEFAULT;
  }

  @Deprecated
  public static Parser failure(String message) {
    return FailureParser.withMessage(message);
  }

  @Deprecated
  public static Parser string(String string) {
    return StringParser.of(string);
  }

  @Deprecated
  public static Parser string(String string, String message) {
    return StringParser.of(string, message);
  }

  @Deprecated
  public static Parser stringIgnoreCase(String string) {
    return StringParser.ofIgnoringCase(string);
  }

  public static Parser stringIgnoreCase(String string, String message) {
    return StringParser.ofIgnoringCase(string, message);
  }

}
