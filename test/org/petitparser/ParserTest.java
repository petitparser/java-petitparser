package org.petitparser;

import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.Chars.character;
import static org.petitparser.Chars.digit;
import static org.petitparser.Chars.upperCase;

import java.util.Arrays;

import org.junit.Test;
import org.petitparser.parser.Parser;

import com.google.common.base.Function;

/**
 * Tests {@link Parsers} factory.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ParserTest {

  @Test
  public void testAnd() {
    Parser parser = character('a').and();
    assertSuccess(parser, "a", 'a', 0);
    assertFailure(parser, "b", "a expected");
    assertFailure(parser, "");
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
  public void testEndOfInput() {
    Parser parser = character('a').end();
    assertFailure(parser, "", "a expected");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "aa", 1, "end of input expected");
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
  public void testNegateDigit() {
    Parser parser = digit().negate("no digit expected");
    assertFailure(parser, "1", 0, "no digit expected");
    assertFailure(parser, "9", 0, "no digit expected");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "", 0, "no digit expected");
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
  public void testPlus() {
    Parser parser = character('a').plus();
    assertFailure(parser, "", "a expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  public void testTimes() {
    Parser parser = character('a').times(2);
    assertFailure(parser, "", "a expected");
    assertFailure(parser, "a", "a expected");
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a'), 2);
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
  public void testStar() {
    Parser parser = character('a').star();
    assertSuccess(parser, "", Arrays.asList());
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
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
  public void testWrapped() {
    Parser parser = character('a').wrapped();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", "a expected");
    assertFailure(parser, "");
  }

  @Test
  public void testSeparatedBy() {
    Parser parser = character('a').separatedBy(character('b'));
    assertFailure(parser, "", "a expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "ab", Arrays.asList('a'), 1);
    assertSuccess(parser, "aba", Arrays.asList('a', 'b', 'a'));
    assertSuccess(parser, "abab", Arrays.asList('a', 'b', 'a'), 3);
    assertSuccess(parser, "ababa", Arrays.asList('a', 'b', 'a', 'b', 'a'));
    assertSuccess(parser, "ababab", Arrays.asList('a', 'b', 'a', 'b', 'a'), 5);
  }

  @Test
  public void testDelimitedBy() {
    Parser parser = character('a').delimitedBy(character('b'));
    assertFailure(parser, "", "a expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "ab", Arrays.asList('a', 'b'));
    assertSuccess(parser, "aba", Arrays.asList('a', 'b', 'a'));
    assertSuccess(parser, "abab", Arrays.asList('a', 'b', 'a', 'b'));
    assertSuccess(parser, "ababa", Arrays.asList('a', 'b', 'a', 'b', 'a'));
    assertSuccess(parser, "ababab", Arrays.asList('a', 'b', 'a', 'b', 'a', 'b'));
  }

}
