package org.petitparser;

import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.List;

/**
 * Deprecated utility methods for parsing input.
 */
@Deprecated
public class Parsing {

  private Parsing() { }

  @Deprecated
  public static Result parse(Parser parser, String string) {
    return parser.parse(string);
  }

  @Deprecated
  public static boolean accepts(Parser parser, String string) {
    return parser.accept(string);
  }

  @Deprecated
  public static <T> List<T> matches(Parser parser, String string) {
    return parser.matches(string);
  }

  @Deprecated
  public static <T> List<T> matchesSkipping(Parser parser, String string) {
    return parser.matchesSkipping(string);
  }

}
