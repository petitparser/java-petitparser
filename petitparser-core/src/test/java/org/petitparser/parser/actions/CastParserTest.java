package org.petitparser.parser.actions;

import org.junit.Test;
import org.petitparser.parser.Parser;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.any;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;

/**
 * Tests for {@link CastParser} and {@link CastListParser}.
 */
public class CastParserTest {

  @Test
  public void testCastDefault() {
    Parser parser = digit().map((Character c) -> Character.getNumericValue(c)).cast();
    assertSuccess(parser, "1", 1);
    assertFailure(parser, "a", 0, "digit expected");
  }

  @Test
  public void testCastWithClass() {
    Parser parser = digit().map((Character c) -> (Number) Character.getNumericValue(c)).cast(Number.class);
    assertSuccess(parser, "1", 1);
    assertFailure(parser, "a", 0, "digit expected");
  }

  @Test
  public void testCastNull() {
    Parser parser = Parser.epsilon(null).cast(String.class);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testCastWithClassMismatchThrows() {
    Parser parser = letter().cast(Integer.class);
    try {
      parser.parse("a");
      fail("Expected ClassCastException");
    } catch (ClassCastException expected) {
      // expected
    }
  }

  @Test
  public void testCastFastParse() {
    Parser parser = digit().cast(Character.class);
    assertEquals(1, parser.fastParseOn("1", 0));
    assertEquals(-1, parser.fastParseOn("a", 0));
    assertEquals(2, parser.fastParseOn("a1", 1));
  }

  @Test
  public void testCastGetters() {
    CastParser<Object, String> defaultParser = new CastParser<>(any());
    assertNull(defaultParser.getTargetClass());

    CastParser<Object, String> classParser = new CastParser<>(any(), String.class);
    assertSame(String.class, classParser.getTargetClass());
  }

  @Test
  public void testCastCopyAndEquality() {
    Parser parser = any().cast(String.class);
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser differentClass = any().cast(Integer.class);
    assertFalse(parser.isEqualTo(differentClass));

    Parser unTyped = any().cast();
    assertFalse(parser.isEqualTo(unTyped));
    assertTrue(unTyped.isEqualTo(unTyped.copy()));

    Parser differentDelegate = digit().cast(String.class);
    assertFalse(parser.isEqualTo(differentDelegate));
  }

  @Test
  public void testCastToString() {
    Parser defaultParser = any().cast();
    assertEquals("CastParser", defaultParser.toString());

    Parser classParser = any().cast(String.class);
    assertEquals("CastParser[String]", classParser.toString());
  }

  @Test
  public void testCastListDefault() {
    Parser parser = digit().times(3).castList();
    assertSuccess(parser, "123", Arrays.asList('1', '2', '3'));
    assertFailure(parser, "12a", 2, "digit expected");
  }

  @Test
  public void testCastListWithClass() {
    Parser parser = digit().times(3).castList(Character.class);
    assertSuccess(parser, "123", Arrays.asList('1', '2', '3'));
    assertFailure(parser, "12a", 2, "digit expected");
  }

  @Test
  public void testCastListMismatchThrows() {
    Parser parser = digit().times(2).castList(Integer.class);
    try {
      parser.parse("12");
      fail("Expected ClassCastException");
    } catch (ClassCastException expected) {
      // expected
    }
  }

  @Test
  public void testCastListFastParse() {
    Parser parser = digit().times(2).castList(Character.class);
    assertEquals(2, parser.fastParseOn("12", 0));
    assertEquals(-1, parser.fastParseOn("1a", 0));
    assertEquals(3, parser.fastParseOn("x12", 1));
  }

  @Test
  public void testCastListGetters() {
    CastListParser<String> defaultParser = new CastListParser<>(any().star());
    assertNull(defaultParser.getElementClass());

    CastListParser<String> classParser = new CastListParser<>(any().star(), String.class);
    assertSame(String.class, classParser.getElementClass());
  }

  @Test
  public void testCastListCopyAndEquality() {
    Parser parser = any().star().castList(String.class);
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser differentClass = any().star().castList(Integer.class);
    assertFalse(parser.isEqualTo(differentClass));

    Parser unTyped = any().star().castList();
    assertFalse(parser.isEqualTo(unTyped));
    assertTrue(unTyped.isEqualTo(unTyped.copy()));

    Parser differentDelegate = digit().star().castList(String.class);
    assertFalse(parser.isEqualTo(differentDelegate));
  }

  @Test
  public void testCastListToString() {
    Parser defaultParser = any().star().castList();
    assertEquals("CastListParser", defaultParser.toString());

    Parser classParser = any().star().castList(String.class);
    assertEquals("CastListParser[String]", classParser.toString());
  }
}
