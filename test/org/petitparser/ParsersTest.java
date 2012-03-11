package org.petitparser;

import static org.petitparser.ParserAssertions.assertFailure;
import static org.petitparser.ParserAssertions.assertSuccess;
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
import java.util.List;

import org.junit.Test;
import org.petitparser.utils.Function;

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
  public void testNegateDigit() {
    Parser<Character> parser = digit().negate("no digit expected");
    assertFailure(parser, "1");
    assertFailure(parser, "9");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
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
    assertSuccess(parser, "", Arrays.<Character> asList());
    assertSuccess(parser, "a", Arrays.<Character> asList('a'));
    assertSuccess(parser, "aa", Arrays.<Character> asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.<Character> asList('a', 'a', 'a'));
  }

  @Test
  public void testPlus() {
    Parser<List<Character>> parser = character('a').plus();
    assertFailure(parser, "");
    assertSuccess(parser, "a", Arrays.<Character> asList('a'));
    assertSuccess(parser, "aa", Arrays.<Character> asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.<Character> asList('a', 'a', 'a'));
  }

  @Test
  public void testChoice2() {
    Parser<Character> parser = character('a').or(character('b'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "c");
    assertFailure(parser, "");
  }

  @Test
  public void testChoice3() {
    Parser<Character> parser = character('a').or(character('b')).or(character('c'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
    assertFailure(parser, "");
  }

  @Test
  public void testSequence2() {
    Parser<List<Character>> parser = character('a').seq(character('b'));
    assertSuccess(parser, "ab", Arrays.<Character> asList('a', 'b'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
  }

  @Test
  public void testSequence3() {
    Parser<List<Character>> parser = character('a').seq(character('b')).seq(character('c'));
    assertSuccess(parser, "abc", Arrays.<Character> asList('a', 'b', 'c'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
    assertFailure(parser, "ab", 2);
    assertFailure(parser, "abx", 2);
  }

  @Test
  public void testFlatten() {
    Parser<String> parser = digit().plus().flatten();
    assertFailure(parser, "");
    assertFailure(parser, "a");
    assertSuccess(parser, "1", "1");
    assertSuccess(parser, "12", "12");
    assertSuccess(parser, "123", "123");
    assertSuccess(parser, "1234", "1234");
  }

  @Test
  public void testMap() {
    Parser<Integer> parser = digit().map(new Function<Character, Integer>() {
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
    Parser<Character> parser = digit().or(upperCase()).negate("no diggit or uppercase expected");
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
    Parser<String> parser = string("foo");
    assertSuccess(parser, "foo", "foo");
    assertFailure(parser, "");
    assertFailure(parser, "f");
    assertFailure(parser, "fo");
    assertFailure(parser, "Foo");
  }

  @Test
  public void testStringIgnoreCase() {
    Parser<String> parser = stringIgnoreCase("foo");
    assertSuccess(parser, "foo", "foo");
    assertSuccess(parser, "FOO", "FOO");
    assertSuccess(parser, "fOo", "fOo");
    assertFailure(parser, "");
    assertFailure(parser, "f");
    assertFailure(parser, "Fo");
  }

  @Test
  public void testEpsilon() {
    Parser<String> parser = epsilon();
    assertSuccess(parser, "", null);
    assertSuccess(parser, "a", null, 0);
  }

  @Test
  public void testFailure() {
    Parser<String> parser = failure("failure");
    assertFailure(parser, "");
    assertFailure(parser, "a");
  }

}
