package org.petitparser.parser.combinators;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.utils.FailureJoiner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests for {@link ChoiceParser} including {@link Parser#orWiden} and static {@link Parser#or}.
 */
public class ChoiceParserTest {

  @Test
  public void testStaticOr2() {
    ChoiceParser parser = Parser.or(digit(), letter());
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "!", 0, "letter expected");
  }

  @Test
  public void testStaticOr3() {
    ChoiceParser parser = Parser.or(digit(), letter(), of('!'));
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "!", '!');
    assertFailure(parser, "?", 0, "'!' expected");
  }

  @Test
  public void testStaticOrWithFailureJoiner() {
    ChoiceParser parser = Parser.or(new FailureJoiner.SelectFirst(), digit(), letter());
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "!", 0, "digit expected");
  }

  @Test
  public void testStaticOrArities4To9() {
    ChoiceParser p4 = Parser.or(of('1'), of('2'), of('3'), of('4'));
    assertSuccess(p4, "4", '4');

    ChoiceParser p5 = Parser.or(of('1'), of('2'), of('3'), of('4'), of('5'));
    assertSuccess(p5, "5", '5');

    ChoiceParser p6 = Parser.or(of('1'), of('2'), of('3'), of('4'), of('5'), of('6'));
    assertSuccess(p6, "6", '6');

    ChoiceParser p7 = Parser.or(of('1'), of('2'), of('3'), of('4'), of('5'), of('6'), of('7'));
    assertSuccess(p7, "7", '7');

    ChoiceParser p8 = Parser.or(of('1'), of('2'), of('3'), of('4'), of('5'), of('6'), of('7'), of('8'));
    assertSuccess(p8, "8", '8');

    ChoiceParser p9 = Parser.or(of('1'), of('2'), of('3'), of('4'), of('5'), of('6'), of('7'), of('8'), of('9'));
    assertSuccess(p9, "9", '9');
    assertFailure(p9, "0", 0, "'9' expected");
  }

  @Test
  public void testStaticOrFailureJoinerArities3To9() {
    FailureJoiner fj = new FailureJoiner.SelectFirst();
    assertSuccess(Parser.or(fj, of('1'), of('2'), of('3')), "3", '3');
    assertSuccess(Parser.or(fj, of('1'), of('2'), of('3'), of('4')), "4", '4');
    assertSuccess(Parser.or(fj, of('1'), of('2'), of('3'), of('4'), of('5')), "5", '5');
    assertSuccess(Parser.or(fj, of('1'), of('2'), of('3'), of('4'), of('5'), of('6')), "6", '6');
    assertSuccess(Parser.or(fj, of('1'), of('2'), of('3'), of('4'), of('5'), of('6'), of('7')), "7", '7');
    assertSuccess(Parser.or(fj, of('1'), of('2'), of('3'), of('4'), of('5'), of('6'), of('7'), of('8')), "8", '8');
    assertSuccess(Parser.or(fj, of('1'), of('2'), of('3'), of('4'), of('5'), of('6'), of('7'), of('8'), of('9')), "9", '9');
  }

  @Test
  public void testOrWiden() {
    Parser intParser = digit().map((Character c) -> (Number) Integer.valueOf(c.toString()));
    Parser doubleParser = letter().map((Character c) -> (Number) Double.valueOf((int) c));
    ChoiceParser widened = intParser.orWiden(doubleParser);

    assertSuccess(widened, "1", 1);
    assertSuccess(widened, "a", (double) (int) 'a');
    assertFailure(widened, "!", 0, "letter expected");
  }

  @Test
  public void testOrWidenWithFailureJoiner() {
    Parser intParser = digit().map((Character c) -> (Number) Integer.valueOf(c.toString()));
    Parser doubleParser = letter().map((Character c) -> (Number) Double.valueOf((int) c));
    ChoiceParser widened = intParser.orWiden(new FailureJoiner.SelectFirst(), doubleParser);

    assertSuccess(widened, "1", 1);
    assertSuccess(widened, "a", (double) (int) 'a');
    assertFailure(widened, "!", 0, "digit expected");
  }

  @Test
  public void testFastParse() {
    ChoiceParser parser = Parser.or(digit(), letter());
    assertEquals(1, parser.fastParseOn("1", 0));
    assertEquals(1, parser.fastParseOn("a", 0));
    assertEquals(-1, parser.fastParseOn("!", 0));
    assertEquals(2, parser.fastParseOn("!1", 1));
  }

  @Test
  public void testCopyAndEquality() {
    ChoiceParser parser = Parser.or(digit(), letter());
    ChoiceParser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    ChoiceParser different = Parser.or(letter(), digit());
    assertFalse(parser.isEqualTo(different));
  }
}
