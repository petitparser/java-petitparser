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

  /**
   * Replace this deprecated call with {@link Parser#parse(String)}.
   */
  @Deprecated
  public static Result parse(Parser parser, String string) {
    return parser.parse(string);
  }

  /**
   * Replace this deprecated call with {@link Parser#accept(String)}.
   */
  @Deprecated
  public static boolean accepts(Parser parser, String string) {
    return parser.accept(string);
  }

  /**
   * Replace this deprecated call with {@link Parser#matches(String)}.
   */
  @Deprecated
  public static <T> List<T> matches(Parser parser, String string) {
    return parser.matches(string);
  }

  /**
   * Replace this deprecated call with {@link Parser#matchesSkipping(String)}.
   */
  @Deprecated
  public static <T> List<T> matchesSkipping(Parser parser, String string) {
    return parser.matchesSkipping(string);
  }
}
