package org.petitparser;

import org.junit.Test;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;
import org.petitparser.parser.combinators.SettableParser;

import java.util.Arrays;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests {@link Parser} factory methods.
 */
public class ParsersTest {

  @Test
  public void testAnd() {
    Parser parser = CharacterParser.is('a').and();
    assertSuccess(parser, "a", 'a', 0);
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "");
  }

  @Test
  public void testChoice2() {
    Parser parser = CharacterParser.is('a').or(CharacterParser.is('b'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "c");
    assertFailure(parser, "");
  }

  @Test
  public void testChoice3() {
    Parser parser = CharacterParser.is('a').or(CharacterParser.is('b')).or(CharacterParser.is('c'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
    assertFailure(parser, "");
  }

  @Test
  public void testEndOfInput() {
    Parser parser = CharacterParser.is('a').end();
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "aa", 1, "end of input expected");
  }

  @Test
  public void testSettable() {
    SettableParser parser = CharacterParser.is('a').settable();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", 0, "'a' expected");
    parser.setDelegate(CharacterParser.is('b'));
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "a", 0, "'b' expected");
  }

  @Test
  public void testFlatten() {
    Parser parser = CharacterParser.digit().plus().flatten();
    assertFailure(parser, "");
    assertFailure(parser, "a");
    assertSuccess(parser, "1", "1");
    assertSuccess(parser, "12", "12");
    assertSuccess(parser, "123", "123");
    assertSuccess(parser, "1234", "1234");
  }

  @Test
  public void testMap() {
    Parser parser =
        CharacterParser.digit().map((Function<Character, Integer>) Character::getNumericValue);
    assertSuccess(parser, "1", 1);
    assertSuccess(parser, "4", 4);
    assertSuccess(parser, "9", 9);
    assertFailure(parser, "");
    assertFailure(parser, "a");
  }

  @Test
  public void testPick1() {
    Parser parser = CharacterParser.digit().seq(CharacterParser.letter()).pick(1);
    assertSuccess(parser, "1a", 'a');
    assertSuccess(parser, "2b", 'b');
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testPick2() {
    Parser parser = CharacterParser.digit().seq(CharacterParser.letter()).pick(-1);
    assertSuccess(parser, "1a", 'a');
    assertSuccess(parser, "2b", 'b');
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testPermute1() {
    Parser parser = CharacterParser.digit().seq(CharacterParser.letter()).permute(1, 0);
    assertSuccess(parser, "1a", Arrays.asList('a', '1'));
    assertSuccess(parser, "2b", Arrays.asList('b', '2'));
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testPermute2() {
    Parser parser = CharacterParser.digit().seq(CharacterParser.letter()).permute(-1, 0);
    assertSuccess(parser, "1a", Arrays.asList('a', '1'));
    assertSuccess(parser, "2b", Arrays.asList('b', '2'));
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testNegate() {
    Parser parser = CharacterParser.digit().or(CharacterParser.upperCase())
        .neg("no digit or uppercase expected");
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
    Parser parser = CharacterParser.digit().neg("no digit expected");
    assertFailure(parser, "1", 0, "no digit expected");
    assertFailure(parser, "9", 0, "no digit expected");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "", 0, "no digit expected");
  }

  @Test
  public void testNot() {
    Parser parser = CharacterParser.is('a').not("not a expected");
    assertFailure(parser, "a", "not a expected");
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testOptional() {
    Parser parser = CharacterParser.is('a').optional();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testPlus() {
    Parser parser = CharacterParser.is('a').plus();
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  @Test
  public void testTimes() {
    Parser parser = CharacterParser.is('a').times(2);
    assertFailure(parser, "", 0, "'a' expected");
    assertFailure(parser, "a", 1, "'a' expected");
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a'), 2);
  }

  @Test
  public void testRepeat() {
    Parser parser = CharacterParser.is('a').repeat(2, 3);
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "a", 1, "'a' expected");
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
    assertSuccess(parser, "aaaa", Arrays.asList('a', 'a', 'a'), 3);
  }

  @Test
  public void testSequence2() {
    Parser parser = CharacterParser.is('a').seq(CharacterParser.is('b'));
    assertSuccess(parser, "ab", Arrays.asList('a', 'b'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
  }

  @Test
  public void testSequence3() {
    Parser parser =
        CharacterParser.is('a').seq(CharacterParser.is('b')).seq(CharacterParser.is('c'));
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
    Parser parser = CharacterParser.is('a').star();
    assertSuccess(parser, "", Arrays.asList());
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  @Test
  public void testToken() {
    Parser parser = CharacterParser.is('a').star().token().trim();
    Token token = parser.parse(" aa ").get();
    assertEquals(1, token.getStart());
    assertEquals(3, token.getStop());
    assertEquals(Arrays.asList('a', 'a'), token.getValue());
  }

  @Test
  public void testTrim() {
    Parser parser = CharacterParser.is('a').trim();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " a", 'a');
    assertSuccess(parser, "a ", 'a');
    assertSuccess(parser, " a ", 'a');
    assertSuccess(parser, "  a", 'a');
    assertSuccess(parser, "a  ", 'a');
    assertSuccess(parser, "  a  ", 'a');
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, " b", 1, "'a' expected");
    assertFailure(parser, "  b", 2, "'a' expected");
  }

  @Test
  public void testTrimCustom() {
    Parser parser = CharacterParser.is('a').trim(CharacterParser.is('*'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "*a", 'a');
    assertSuccess(parser, "a*", 'a');
    assertSuccess(parser, "*a*", 'a');
    assertSuccess(parser, "**a", 'a');
    assertSuccess(parser, "a**", 'a');
    assertSuccess(parser, "**a**", 'a');
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "*b", 1, "'a' expected");
    assertFailure(parser, "**b", 2, "'a' expected");
  }

  @Test
  public void testSeparatedBy() {
    Parser parser = CharacterParser.is('a').separatedBy(CharacterParser.is('b'));
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "ab", Arrays.asList('a'), 1);
    assertSuccess(parser, "aba", Arrays.asList('a', 'b', 'a'));
    assertSuccess(parser, "abab", Arrays.asList('a', 'b', 'a'), 3);
    assertSuccess(parser, "ababa", Arrays.asList('a', 'b', 'a', 'b', 'a'));
    assertSuccess(parser, "ababab", Arrays.asList('a', 'b', 'a', 'b', 'a'), 5);
  }

  @Test
  public void testDelimitedBy() {
    Parser parser = CharacterParser.is('a').delimitedBy(CharacterParser.is('b'));
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "ab", Arrays.asList('a', 'b'));
    assertSuccess(parser, "aba", Arrays.asList('a', 'b', 'a'));
    assertSuccess(parser, "abab", Arrays.asList('a', 'b', 'a', 'b'));
    assertSuccess(parser, "ababa", Arrays.asList('a', 'b', 'a', 'b', 'a'));
    assertSuccess(parser, "ababab", Arrays.asList('a', 'b', 'a', 'b', 'a', 'b'));
  }
}
