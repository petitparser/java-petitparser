package org.petitparser;

import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;

/**
 * Deprecated factory for character parsers.
 */
@Deprecated
public class Chars {

  private Chars() {
  }

  @Deprecated
  public static Parser any() {
    return CharacterParser.any();
  }

  @Deprecated
  public static Parser any(String message) {
    return CharacterParser.any(message);
  }

  @Deprecated
  public static Parser anyOf(String characters) {
    return CharacterParser.anyOf(characters);
  }

  @Deprecated
  public static Parser anyOf(String characters, String message) {
    return CharacterParser.anyOf(characters, message);
  }

  @Deprecated
  public static Parser character(char character) {
    return CharacterParser.is(character);
  }

  @Deprecated
  public static Parser character(char character, String message) {
    return CharacterParser.is(character, message);
  }

  @Deprecated
  public static Parser digit() {
    return CharacterParser.digit();
  }

  @Deprecated
  public static Parser digit(String message) {
    return CharacterParser.digit(message);
  }

  @Deprecated
  public static Parser letter() {
    return CharacterParser.letter();
  }

  @Deprecated
  public static Parser letter(String message) {
    return CharacterParser.letter(message);
  }

  @Deprecated
  public static Parser lowerCase() {
    return CharacterParser.lowerCase();
  }

  @Deprecated
  public static Parser lowerCase(String message) {
    return CharacterParser.lowerCase(message);
  }

  @Deprecated
  public static Parser pattern(String pattern) {
    return CharacterParser.pattern(pattern);
  }

  @Deprecated
  public static Parser pattern(String pattern, String message) {
    return CharacterParser.pattern(pattern, message);
  }

  @Deprecated
  public static Parser range(char start, char stop) {
    return CharacterParser.range(start, stop);
  }

  @Deprecated
  public static Parser range(char start, char stop, String message) {
    return CharacterParser.range(start, stop, message);
  }

  @Deprecated
  public static Parser upperCase() {
    return CharacterParser.upperCase();
  }

  @Deprecated
  public static Parser upperCase(String message) {
    return CharacterParser.upperCase(message);
  }

  @Deprecated
  public static Parser whitespace() {
    return CharacterParser.whitespace();
  }

  @Deprecated
  public static Parser whitespace(String message) {
    return CharacterParser.whitespace(message);
  }

  @Deprecated
  public static Parser word() {
    return CharacterParser.word();
  }

  @Deprecated
  public static Parser word(String message) {
    return CharacterParser.word(message);
  }

}
