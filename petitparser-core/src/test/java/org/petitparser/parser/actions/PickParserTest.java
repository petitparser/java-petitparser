package org.petitparser.parser.actions;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.utils.tuples.Tuple2;
import org.petitparser.utils.tuples.Tuple3;
import org.petitparser.utils.tuples.Tuple4;
import org.petitparser.utils.tuples.Tuple5;
import org.petitparser.utils.tuples.Tuple6;
import org.petitparser.utils.tuples.Tuple7;
import org.petitparser.utils.tuples.Tuple8;
import org.petitparser.utils.tuples.Tuple9;

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

    Object result = parser.parse("1a").get();
    assertTrue(result instanceof Tuple2);
    Tuple2<?, ?> tuple = (Tuple2<?, ?>) result;
    assertEquals('a', tuple.first());
    assertEquals('1', tuple.second());
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
    assertTrue(res3 instanceof Tuple3);
    assertEquals(Arrays.asList('2', '1', 'a'), res3);

    // 4 elements returning Tuple4
    Parser p4 = digit().times(4).permute(3, 2, 1, 0);
    Object res4 = p4.parse("1234").get();
    assertTrue(res4 instanceof Tuple4);
    assertEquals(Arrays.asList('4', '3', '2', '1'), res4);

    // 5 elements returning Tuple5
    Parser p5 = digit().times(5).permute(4, 3, 2, 1, 0);
    Object res5 = p5.parse("12345").get();
    assertTrue(res5 instanceof Tuple5);
    assertEquals(Arrays.asList('5', '4', '3', '2', '1'), res5);

    // 6 elements returning Tuple6
    Parser p6 = digit().times(6).permute(5, 4, 3, 2, 1, 0);
    Object res6 = p6.parse("123456").get();
    assertTrue(res6 instanceof Tuple6);
    assertEquals(Arrays.asList('6', '5', '4', '3', '2', '1'), res6);

    // 7 elements returning Tuple7
    Parser p7 = digit().times(7).permute(6, 5, 4, 3, 2, 1, 0);
    Object res7 = p7.parse("1234567").get();
    assertTrue(res7 instanceof Tuple7);
    assertEquals(Arrays.asList('7', '6', '5', '4', '3', '2', '1'), res7);

    // 8 elements returning Tuple8
    Parser p8 = digit().times(8).permute(7, 6, 5, 4, 3, 2, 1, 0);
    Object res8 = p8.parse("12345678").get();
    assertTrue(res8 instanceof Tuple8);
    assertEquals(Arrays.asList('8', '7', '6', '5', '4', '3', '2', '1'), res8);

    // 9 elements returning Tuple9
    Parser p9 = digit().times(9).permute(8, 7, 6, 5, 4, 3, 2, 1, 0);
    Object res9 = p9.parse("123456789").get();
    assertTrue(res9 instanceof Tuple9);
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
  @SuppressWarnings("deprecation")
  public void testPermuteGetters() {
    PermuteParser parser = new PermuteParser(digit().star(), 1, -1, 0);
    assertArrayEquals(new int[]{1, -1, 0}, parser.getIndices());
    assertArrayEquals(new int[]{1, -1, 0}, parser.getIndexes());
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
}
