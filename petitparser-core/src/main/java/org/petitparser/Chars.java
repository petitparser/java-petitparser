package org.petitparser;

import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;

/**
 * Deprecated factory for character parsers.
 */
@Deprecated
public class Chars {

  private Chars() { }

  /**
   * Replace this deprecated call with {@link CharacterParser#any()}.
   */
  @Deprecated
  public static Parser any() {
    return CharacterParser.any();
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#any(String)}.
   */
  @Deprecated
  public static Parser any(String message) {
    return CharacterParser.any(message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#anyOf(String)}.
   */
  @Deprecated
  public static Parser anyOf(String characters) {
    return CharacterParser.anyOf(characters);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#anyOf(String, String)}.
   */
  @Deprecated
  public static Parser anyOf(String characters, String message) {
    return CharacterParser.anyOf(characters, message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#of(char)}.
   */
  @Deprecated
  public static Parser character(char character) {
    return CharacterParser.of(character);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#of(char, String)}.
   */
  @Deprecated
  public static Parser character(char character, String message) {
    return CharacterParser.of(character, message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#digit()}.
   */
  @Deprecated
  public static Parser digit() {
    return CharacterParser.digit();
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#digit(String)}.
   */
  @Deprecated
  public static Parser digit(String message) {
    return CharacterParser.digit(message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#letter()}.
   */
  @Deprecated
  public static Parser letter() {
    return CharacterParser.letter();
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#letter(String)}.
   */
  @Deprecated
  public static Parser letter(String message) {
    return CharacterParser.letter(message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#lowerCase()}.
   */
  @Deprecated
  public static Parser lowerCase() {
    return CharacterParser.lowerCase();
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#lowerCase(String)}.
   */
  @Deprecated
  public static Parser lowerCase(String message) {
    return CharacterParser.lowerCase(message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#pattern(String)}.
   */
  @Deprecated
  public static Parser pattern(String pattern) {
    return CharacterParser.pattern(pattern);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#pattern(String, String)}.
   */
  @Deprecated
  public static Parser pattern(String pattern, String message) {
    return CharacterParser.pattern(pattern, message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#range(char, char)}.
   */
  @Deprecated
  public static Parser range(char start, char stop) {
    return CharacterParser.range(start, stop);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#range(char, char, String)}.
   */
  @Deprecated
  public static Parser range(char start, char stop, String message) {
    return CharacterParser.range(start, stop, message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#upperCase()}.
   */
  @Deprecated
  public static Parser upperCase() {
    return CharacterParser.upperCase();
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#upperCase(String)}.
   */
  @Deprecated
  public static Parser upperCase(String message) {
    return CharacterParser.upperCase(message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#whitespace()}.
   */
  @Deprecated
  public static Parser whitespace() {
    return CharacterParser.whitespace();
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#whitespace(String)}.
   */
  @Deprecated
  public static Parser whitespace(String message) {
    return CharacterParser.whitespace(message);
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#word()}.
   */
  @Deprecated
  public static Parser word() {
    return CharacterParser.word();
  }

  /**
   * Replace this deprecated call with {@link CharacterParser#word(String)}.
   */
  @Deprecated
  public static Parser word(String message) {
    return CharacterParser.word(message);
  }
}
