package org.petitparser;


/**
 * Factory for various parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Parsers {

  /**
   * Returns a parser that parses a single {@code character}.
   */
  public static Parser<String> character(char character) {
    return string(Character.toString(character));
  }

  /**
   * Returns a parser that parses a {@code string}.
   */
  public static Parser<String> string(String string) {
    return new StringParser(string);
  }

}
