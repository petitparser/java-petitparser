package org.petitparser.parser.actions;

import org.junit.Test;
import org.petitparser.parser.Parser;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;

/**
 * Tests for {@link PickParser} and {@link PermuteParser}.
 */
public class PickParserTest {

  @Test
  public void testPickPositive() {
    Parser parser = digit().seq(letter()).pick(1);
    assertSuccess(parser, "1a", 'a');
    assertSuccess(parser, "2b", 'b');
    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testPickNegative() {
    Parser parser = digit().seq(letter()).pick(-1);
    assertSuccess(parser, "1a", 'a');
    assertSuccess(parser, "2b", 'b');

    Parser first = digit().seq(letter()).pick(-2);
    assertSuccess(first, "1a", '1');
    assertSuccess(first, "2b", '2');
  }

  @Test
  public void testPickFastParse() {
    Parser parser = digit().seq(letter()).pick(0);
    assertEquals(2, parser.fastParseOn("1a", 0));
    assertEquals(-1, parser.fastParseOn("12", 0));
    assertEquals(3, parser.fastParseOn("x1a", 1));
  }

  @Test
  public void testPickGetters() {
    PickParser<Object> parser = new PickParser<>(digit().star(), 3);
    assertEquals(3, parser.getIndex());
  }

  @Test
  public void testPickCopyAndEquality() {
    Parser parser = digit().seq(letter()).pick(1);
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser differentIndex = digit().seq(letter()).pick(0);
    assertFalse(parser.isEqualTo(differentIndex));

    Parser differentDelegate = letter().seq(digit()).pick(1);
    assertFalse(parser.isEqualTo(differentDelegate));
  }

  @Test
  public void testPickToString() {
    Parser parser = digit().seq(letter()).pick(1);
    assertEquals("PickParser[1]", parser.toString());
  }

  @Test
  public void testPermuteFromStart() {
    Parser parser = digit().seq(letter()).permute(1, 0);
    assertSuccess(parser, "1a", Arrays.asList('a', '1'));
    assertSuccess(parser, "2b", Arrays.asList('b', '2'));
    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");

    List<Object> result = parser.parse("1a").get();
    assertEquals('a', result.get(0));
    assertEquals('1', result.get(1));
  }

  @Test
  public void testPermuteFromEnd() {
    Parser parser = digit().seq(letter()).permute(-1, 0);
    assertSuccess(parser, "1a", Arrays.asList('a', '1'));
    assertSuccess(parser, "2b", Arrays.asList('b', '2'));
  }

  @Test
  public void testPermuteRepeated() {
    Parser parser = digit().seq(letter()).permute(1, 1);
    assertSuccess(parser, "1a", Arrays.asList('a', 'a'));
    assertSuccess(parser, "2b", Arrays.asList('b', 'b'));
  }

  @Test
  public void testPermuteEmptyAndSingle() {
    Parser emptyParser = digit().seq(letter()).permute();
    assertSuccess(emptyParser, "1a", Collections.emptyList());

    Parser singleParser = digit().seq(letter()).permute(1);
    assertSuccess(singleParser, "1a", Collections.singletonList('a'));
  }

  @Test
  public void testPermuteArities() {
    // 3 elements returning Tuple3
    Parser p3 = Parser.seq(digit(), letter(), digit()).permute(2, 0, 1);
    Object res3 = p3.parse("1a2").get();
    assertEquals(Arrays.asList('2', '1', 'a'), res3);

    // 4 elements returning Tuple4
    Parser p4 = digit().times(4).permute(3, 2, 1, 0);
    Object res4 = p4.parse("1234").get();
    assertEquals(Arrays.asList('4', '3', '2', '1'), res4);

    // 5 elements returning Tuple5
    Parser p5 = digit().times(5).permute(4, 3, 2, 1, 0);
    Object res5 = p5.parse("12345").get();
    assertEquals(Arrays.asList('5', '4', '3', '2', '1'), res5);

    // 6 elements returning Tuple6
    Parser p6 = digit().times(6).permute(5, 4, 3, 2, 1, 0);
    Object res6 = p6.parse("123456").get();
    assertEquals(Arrays.asList('6', '5', '4', '3', '2', '1'), res6);

    // 7 elements returning Tuple7
    Parser p7 = digit().times(7).permute(6, 5, 4, 3, 2, 1, 0);
    Object res7 = p7.parse("1234567").get();
    assertEquals(Arrays.asList('7', '6', '5', '4', '3', '2', '1'), res7);

    // 8 elements returning Tuple8
    Parser p8 = digit().times(8).permute(7, 6, 5, 4, 3, 2, 1, 0);
    Object res8 = p8.parse("12345678").get();
    assertEquals(Arrays.asList('8', '7', '6', '5', '4', '3', '2', '1'), res8);

    // 9 elements returning Tuple9
    Parser p9 = digit().times(9).permute(8, 7, 6, 5, 4, 3, 2, 1, 0);
    Object res9 = p9.parse("123456789").get();
    assertEquals(Arrays.asList('9', '8', '7', '6', '5', '4', '3', '2', '1'), res9);

    // 10 elements (>9 returns List)
    Parser p10 = digit().times(10).permute(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
    List<?> res10 = p10.parse("0123456789").get();
    assertEquals(Arrays.asList('9', '8', '7', '6', '5', '4', '3', '2', '1', '0'), res10);
  }

  @Test
  public void testPermuteFastParse() {
    Parser parser = digit().seq(letter()).permute(1, 0);
    assertEquals(2, parser.fastParseOn("1a", 0));
    assertEquals(-1, parser.fastParseOn("11", 0));
    assertEquals(3, parser.fastParseOn("x1a", 1));
  }

  @Test
  public void testPermuteGetters() {
    PermuteParser parser = new PermuteParser(digit().star(), 1, -1, 0);
    assertArrayEquals(new int[]{1, -1, 0}, parser.getIndices());
  }

  @Test
  public void testPermuteCopyAndEquality() {
    Parser parser = digit().seq(letter()).permute(1, 0);
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser differentIndices = digit().seq(letter()).permute(0, 1);
    assertFalse(parser.isEqualTo(differentIndices));

    Parser differentDelegate = letter().seq(digit()).permute(1, 0);
    assertFalse(parser.isEqualTo(differentDelegate));
  }

  @Test
  public void testPermuteToString() {
    Parser parser = digit().seq(letter()).permute(1, 0);
    assertEquals("PermuteParser[1, 0]", parser.toString());
  }

  @Test
  public void testPermuteNegativeIndices() {
    Parser singleNegative = digit().seq(letter()).permute(-1);
    assertEquals(Collections.singletonList('a'), singleNegative.parse("1a").get());

    Parser tenNegative = digit().repeat(10, 10).permute(-1, -2, -3, -4, -5, -6, -7, -8, -9, -10);
    assertEquals(
        Arrays.asList('9', '8', '7', '6', '5', '4', '3', '2', '1', '0'),
        tenNegative.parse("0123456789").get());
  }
}
