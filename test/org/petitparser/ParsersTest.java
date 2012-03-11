package org.petitparser;

import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.Parsers.any;
import static org.petitparser.Parsers.character;
import static org.petitparser.Parsers.digit;
import static org.petitparser.Parsers.epsilon;
import static org.petitparser.Parsers.failure;
import static org.petitparser.Parsers.letter;
import static org.petitparser.Parsers.lowerCase;
import static org.petitparser.Parsers.string;
import static org.petitparser.Parsers.stringIgnoreCase;
import static org.petitparser.Parsers.upperCase;
import static org.petitparser.Parsers.whitespace;
import static org.petitparser.Parsers.word;

import java.util.Arrays;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.utils.Function;

/**
 * Tests {@link Parser} and {@link Parsers} and all implementing classes.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ParsersTest {

  @Test
  public void testCharacter() {
    Parser parser = character('a');
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b");
    assertFailure(parser, "");
  }

  @Test
  public void testAny() {
    Parser parser = any();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "input expected");
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
  public void testWord() {
    Parser parser = word();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "0", '0');
    assertFailure(parser, "-", "letter or digit expected");
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
  public void testNegateDigit() {
    Parser parser = digit().negate("no digit expected");
    assertFailure(parser, "1", 0, "no digit expected");
    assertFailure(parser, "9", 0, "no digit expected");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "", 0, "no digit expected");
  }

  @Test
  public void testWrapped() {
    Parser parser = character('a').wrapped();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", "a expected");
    assertFailure(parser, "");

  }

  @Test
  public void testAnd() {
    Parser parser = character('a').and();
    assertSuccess(parser, "a", 'a', 0);
    assertFailure(parser, "b", "a expected");
    assertFailure(parser, "");
  }

  @Test
  public void testNot() {
    Parser parser = character('a').not("not a expected");
    assertFailure(parser, "a", "not a expected");
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testOptional() {
    Parser parser = character('a').optional();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testStar() {
    Parser parser = character('a').star();
    assertSuccess(parser, "", Arrays.asList());
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  @Test
  public void testPlus() {
    Parser parser = character('a').plus();
    assertFailure(parser, "", "a expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  @Test
  public void testRepeat() {
    Parser parser = character('a').repeat(2, 3);
    assertFailure(parser, "", "a expected");
    assertFailure(parser, "a", 1, "a expected");
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
    assertSuccess(parser, "aaaa", Arrays.asList('a', 'a', 'a'), 3);
  }

  @Test
  public void testChoice2() {
    Parser parser = character('a').or(character('b'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "c");
    assertFailure(parser, "");
  }

  @Test
  public void testChoice3() {
    Parser parser = character('a').or(character('b')).or(character('c'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
    assertFailure(parser, "");
  }

  @Test
  public void testSequence2() {
    Parser parser = character('a').seq(character('b'));
    assertSuccess(parser, "ab", Arrays.asList('a', 'b'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
  }

  @Test
  public void testSequence3() {
    Parser parser = character('a').seq(character('b')).seq(character('c'));
    assertSuccess(parser, "abc", Arrays.asList('a', 'b', 'c'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
    assertFailure(parser, "ab", 2);
    assertFailure(parser, "abx", 2);
  }

  @Test
  public void testFlatten() {
    Parser parser = digit().plus().flatten();
    assertFailure(parser, "");
    assertFailure(parser, "a");
    assertSuccess(parser, "1", "1");
    assertSuccess(parser, "12", "12");
    assertSuccess(parser, "123", "123");
    assertSuccess(parser, "1234", "1234");
  }

  @Test
  public void testMap() {
    Parser parser = digit().map(new Function<Character, Integer>() {
      @Override
      public Integer apply(Character argument) {
        return Character.getNumericValue(argument);
      }
    });
    assertSuccess(parser, "1", 1);
    assertSuccess(parser, "4", 4);
    assertSuccess(parser, "9", 9);
    assertFailure(parser, "");
    assertFailure(parser, "a");
  }

  @Test
  public void testNegate() {
    Parser parser = digit().or(upperCase()).negate(
        "no diggit or uppercase expected");
    assertFailure(parser, "1");
    assertFailure(parser, "9");
    assertFailure(parser, "A");
    assertFailure(parser, "Z");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "");
  }

  @Test
  public void testString() {
    Parser parser = string("foo");
    assertSuccess(parser, "foo", "foo");
    assertFailure(parser, "");
    assertFailure(parser, "f");
    assertFailure(parser, "fo");
    assertFailure(parser, "Foo");
  }

  @Test
  public void testStringIgnoreCase() {
    Parser parser = stringIgnoreCase("foo");
    assertSuccess(parser, "foo", "foo");
    assertSuccess(parser, "FOO", "FOO");
    assertSuccess(parser, "fOo", "fOo");
    assertFailure(parser, "");
    assertFailure(parser, "f");
    assertFailure(parser, "Fo");
  }

  @Test
  public void testEpsilon() {
    Parser parser = epsilon();
    assertSuccess(parser, "", null);
    assertSuccess(parser, "a", null, 0);
  }

  @Test
  public void testFailure() {
    Parser parser = failure("failure");
    assertFailure(parser, "");
    assertFailure(parser, "a");
  }

  @Test
  public void testTrim() {
    Parser parser = character('a').trim();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " a", 'a');
    assertSuccess(parser, "a ", 'a');
    assertSuccess(parser, " a ", 'a');
    assertSuccess(parser, "  a", 'a');
    assertSuccess(parser, "a  ", 'a');
    assertSuccess(parser, "  a  ", 'a');
    assertFailure(parser, "", "a expected");
    assertFailure(parser, "b", "a expected");
    assertFailure(parser, " b", 1, "a expected");
    assertFailure(parser, "  b", 2, "a expected");
  }

  @Test
  public void testTrimCustom() {
    Parser parser = character('a').trim(character('*'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "*a", 'a');
    assertSuccess(parser, "a*", 'a');
    assertSuccess(parser, "*a*", 'a');
    assertSuccess(parser, "**a", 'a');
    assertSuccess(parser, "a**", 'a');
    assertSuccess(parser, "**a**", 'a');
    assertFailure(parser, "", "a expected");
    assertFailure(parser, "b", "a expected");
    assertFailure(parser, "*b", 1, "a expected");
    assertFailure(parser, "**b", 2, "a expected");
  }

  @Test
  public void testEndOfInput() {
    Parser parser = character('a').end();
    assertFailure(parser, "", "a expected");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "aa", 1, "end of input expected");
  }

}
