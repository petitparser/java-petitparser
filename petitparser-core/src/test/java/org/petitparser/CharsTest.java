package org.petitparser;

import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.Chars.any;
import static org.petitparser.Chars.character;
import static org.petitparser.Chars.digit;
import static org.petitparser.Chars.letter;
import static org.petitparser.Chars.lowerCase;
import static org.petitparser.Chars.pattern;
import static org.petitparser.Chars.range;
import static org.petitparser.Chars.upperCase;
import static org.petitparser.Chars.whitespace;
import static org.petitparser.Chars.word;

import org.junit.Test;
import org.petitparser.parser.Parser;

/**
 * Tests {@link Chars} factory.
 */
public class CharsTest {

  @Test
  public void testAny() {
    Parser parser = any();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "character expected");
  }

  @Test
  public void testCharacter() {
    Parser parser = character('a');
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b");
    assertFailure(parser, "");
  }

  @Test
  public void testDigit() {
    Parser parser = digit();
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "9", '9');
    assertFailure(parser, "a", "digit expected");
    assertFailure(parser, "");
  }

  @Test
  public void testLetter() {
    Parser parser = letter();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "X", 'X');
    assertFailure(parser, "0", "letter expected");
    assertFailure(parser, "");
  }

  @Test
  public void testLowerCase() {
    Parser parser = lowerCase();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "A", "lowercase letter expected");
    assertFailure(parser, "0", "lowercase letter expected");
    assertFailure(parser, "");
  }

  @Test
  public void testPattern1() {
    Parser parser = pattern("abc");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
  }

  @Test
  public void testPattern2() {
    Parser parser = pattern("a-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
  }

  @Test
  public void testPattern3() {
    Parser parser = pattern("^a-cd");
    assertFailure(parser, "a");
    assertFailure(parser, "b");
    assertFailure(parser, "c");
    assertFailure(parser, "d");
    assertSuccess(parser, "e", 'e');
  }

  @Test
  public void testRange() {
    Parser parser = range('e', 'o');
    assertFailure(parser, "d", "e..o expected");
    assertSuccess(parser, "e", 'e');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertFailure(parser, "p", "e..o expected");
  }

  @Test
  public void testUpperCase() {
    Parser parser = upperCase();
    assertSuccess(parser, "Z", 'Z');
    assertFailure(parser, "z", "uppercase letter expected");
    assertFailure(parser, "0", "uppercase letter expected");
    assertFailure(parser, "");
  }

  @Test
  public void testWhitespace() {
    Parser parser = whitespace();
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "z", "whitespace expected");
    assertFailure(parser, "-", "whitespace expected");
    assertFailure(parser, "");
  }

  @Test
  public void testWord() {
    Parser parser = word();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "0", '0');
    assertFailure(parser, "-", "letter or digit expected");
    assertFailure(parser, "");
  }

}
