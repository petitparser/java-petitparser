package org.petitparser;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;

import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests {@link CharacterParser}.
 */
public class CharactersTest {

  @Test
  public void testAny() {
    Parser parser = CharacterParser.any();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "character expected");
  }

  @Test
  public void testAnyOf() {
    Parser parser = CharacterParser.anyOf("uncopyrightable");
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "g", 'g');
    assertSuccess(parser, "h", 'h');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertSuccess(parser, "p", 'p');
    assertSuccess(parser, "r", 'r');
    assertSuccess(parser, "t", 't');
    assertSuccess(parser, "y", 'y');
    assertFailure(parser, "x", "character expected");
  }

  @Test
  public void testNone() {
    Parser parser = CharacterParser.none();
    assertFailure(parser, "a", "no character expected");
    assertFailure(parser, "b", "no character expected");
    assertFailure(parser, "", "no character expected");
  }

  @Test
  public void testNoneOf() {
    Parser parser = CharacterParser.noneOf("uncopyrightable");
    assertSuccess(parser, "x", 'x');
    assertFailure(parser, "c", "character expected");
    assertFailure(parser, "g", "character expected");
    assertFailure(parser, "h", "character expected");
    assertFailure(parser, "i", "character expected");
    assertFailure(parser, "o", "character expected");
    assertFailure(parser, "p", "character expected");
    assertFailure(parser, "r", "character expected");
    assertFailure(parser, "t", "character expected");
    assertFailure(parser, "y", "character expected");
  }

  @Test
  public void testCharacter() {
    Parser parser = CharacterParser.is('a');
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b");
    assertFailure(parser, "");
  }

  @Test
  public void testDigit() {
    Parser parser = CharacterParser.digit();
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "9", '9');
    assertFailure(parser, "a", "digit expected");
    assertFailure(parser, "");
  }

  @Test
  public void testLetter() {
    Parser parser = CharacterParser.letter();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "X", 'X');
    assertFailure(parser, "0", "letter expected");
    assertFailure(parser, "");
  }

  @Test
  public void testLowerCase() {
    Parser parser = CharacterParser.lowerCase();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "A", "lowercase letter expected");
    assertFailure(parser, "0", "lowercase letter expected");
    assertFailure(parser, "");
  }

  @Test
  public void testPattern1() {
    Parser parser = CharacterParser.pattern("abc");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
  }

  @Test
  public void testPattern2() {
    Parser parser = CharacterParser.pattern("a-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
  }

  @Test
  public void testPattern3() {
    Parser parser = CharacterParser.pattern("^a-cd");
    assertFailure(parser, "a");
    assertFailure(parser, "b");
    assertFailure(parser, "c");
    assertFailure(parser, "d");
    assertSuccess(parser, "e", 'e');
  }

  @Test
  public void testRange() {
    Parser parser = CharacterParser.range('e', 'o');
    assertFailure(parser, "d", "e..o expected");
    assertSuccess(parser, "e", 'e');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertFailure(parser, "p", "e..o expected");
  }

  @Test
  public void testUpperCase() {
    Parser parser = CharacterParser.upperCase();
    assertSuccess(parser, "Z", 'Z');
    assertFailure(parser, "z", "uppercase letter expected");
    assertFailure(parser, "0", "uppercase letter expected");
    assertFailure(parser, "");
  }

  @Test
  public void testWhitespace() {
    Parser parser = CharacterParser.whitespace();
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "z", "whitespace expected");
    assertFailure(parser, "-", "whitespace expected");
    assertFailure(parser, "");
  }

  @Test
  public void testWord() {
    Parser parser = CharacterParser.word();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "0", '0');
    assertFailure(parser, "-", "letter or digit expected");
    assertFailure(parser, "");
  }

}
