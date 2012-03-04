package org.petitparser;

import static org.petitparser.ParserAssertions.assertFailure;
import static org.petitparser.ParserAssertions.assertSuccess;
import static org.petitparser.Parsers.any;
import static org.petitparser.Parsers.character;
import static org.petitparser.Parsers.digit;
import static org.petitparser.Parsers.letter;
import static org.petitparser.Parsers.lowerCase;
import static org.petitparser.Parsers.upperCase;
import static org.petitparser.Parsers.whitespace;
import static org.petitparser.Parsers.word;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Tests {@link Parser} and {@link Parsers} and all implementing classes.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ParsersTest {

  @Test
  public void testCharacter() {
    Parser<Character> parser = character('a');
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b");
    assertFailure(parser, "");
  }

  @Test
  public void testAny() {
    Parser<Character> parser = any();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "");
  }

  @Test
  public void testDigit() {
    Parser<Character> parser = digit();
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "9", '9');
    assertFailure(parser, "a");
    assertFailure(parser, "");
  }

  @Test
  public void testLetter() {
    Parser<Character> parser = letter();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "X", 'X');
    assertFailure(parser, "0");
    assertFailure(parser, "");
  }

  @Test
  public void testWord() {
    Parser<Character> parser = word();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "0", '0');
    assertFailure(parser, "-");
    assertFailure(parser, "");
  }

  @Test
  public void testLowerCase() {
    Parser<Character> parser = lowerCase();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "A");
    assertFailure(parser, "0");
    assertFailure(parser, "");
  }

  @Test
  public void testUpperCase() {
    Parser<Character> parser = upperCase();
    assertSuccess(parser, "Z", 'Z');
    assertFailure(parser, "z");
    assertFailure(parser, "0");
    assertFailure(parser, "");
  }

  @Test
  public void testWhitespace() {
    Parser<Character> parser = whitespace();
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "z");
    assertFailure(parser, "-");
    assertFailure(parser, "");
  }

  @Test
  public void testWrapped() {
    Parser<Character> parser = character('a').wrapped();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b");
    assertFailure(parser, "");

  }

  @Test
  public void testAnd() {
    Parser<Character> parser = character('a').and();
    assertSuccess(parser, "a", 'a', 0);
    assertFailure(parser, "b");
    assertFailure(parser, "");

  }

  @Test
  public void testNot() {
    Parser<Character> parser = character('a').not("Not a");
    assertFailure(parser, "a");
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testOptional() {
    Parser<Character> parser = character('a').optional();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testStar() {
    Parser<List<Character>> parser = character('a').star();
    assertSuccess(parser, "", Arrays.<Character>asList());
    assertSuccess(parser, "a", Arrays.<Character>asList('a'));
    assertSuccess(parser, "aa", Arrays.<Character>asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.<Character>asList('a', 'a', 'a'));
  }

  @Test
  public void testPlus() {
    Parser<List<Character>> parser = character('a').plus();
    assertFailure(parser, "");
    assertSuccess(parser, "a", Arrays.<Character>asList('a'));
    assertSuccess(parser, "aa", Arrays.<Character>asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.<Character>asList('a', 'a', 'a'));
  }

}
