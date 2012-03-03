package org.petitparser;

import static org.petitparser.ParserAssertions.assertFail;
import static org.petitparser.ParserAssertions.assertParse;
import static org.petitparser.Parsers.any;
import static org.petitparser.Parsers.character;
import static org.petitparser.Parsers.digit;
import static org.petitparser.Parsers.letter;
import static org.petitparser.Parsers.lowerCase;
import static org.petitparser.Parsers.upperCase;
import static org.petitparser.Parsers.whitespace;
import static org.petitparser.Parsers.word;

import org.junit.Test;

/**
 * Tests {@link Parser} and {@link Parsers} and all implementing classes.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ParsersTests {

  @Test
  public void testCharacter() {
    Parser<Character> parser = character('a');
    assertParse(parser, "a", 'a');
    assertFail(parser, "b");
    assertFail(parser, "");
  }

  @Test
  public void testAny() {
    Parser<Character> parser = any();
    assertParse(parser, "a", 'a');
    assertParse(parser, "b", 'b');
    assertFail(parser, "");
  }

  @Test
  public void testDigit() {
    Parser<Character> parser = digit();
    assertParse(parser, "1", '1');
    assertParse(parser, "9", '9');
    assertFail(parser, "a");
    assertFail(parser, "");
  }

  @Test
  public void testLetter() {
    Parser<Character> parser = letter();
    assertParse(parser, "a", 'a');
    assertParse(parser, "X", 'X');
    assertFail(parser, "0");
    assertFail(parser, "");
  }

  @Test
  public void testWord() {
    Parser<Character> parser = word();
    assertParse(parser, "a", 'a');
    assertParse(parser, "0", '0');
    assertFail(parser, "-");
    assertFail(parser, "");
  }

  @Test
  public void testLowerCase() {
    Parser<Character> parser = lowerCase();
    assertParse(parser, "a", 'a');
    assertFail(parser, "A");
    assertFail(parser, "0");
    assertFail(parser, "");
  }

  @Test
  public void testUpperCase() {
    Parser<Character> parser = upperCase();
    assertParse(parser, "Z", 'Z');
    assertFail(parser, "z");
    assertFail(parser, "0");
    assertFail(parser, "");
  }

  @Test
  public void testWhitespace() {
    Parser<Character> parser = whitespace();
    assertParse(parser, " ", ' ');
    assertFail(parser, "z");
    assertFail(parser, "-");
    assertFail(parser, "");
  }

}
