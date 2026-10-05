package org.petitparser.parser.primitive;

import org.junit.Test;
import org.petitparser.parser.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests for {@link EpsilonParser} and primitive factories.
 */
public class EpsilonParserTest {

  @Test
  public void testEpsilonDefault() {
    Parser parser = Parser.epsilon();
    assertSame(EpsilonParser.INSTANCE, parser);
    assertNull(((EpsilonParser) parser).getValue());
    assertEquals("EpsilonParser", parser.toString());

    assertSuccess(parser, "", null, 0);
    assertSuccess(parser, "a", null, 0);
  }

  @Test
  public void testEpsilonWithValue() {
    Parser parser = Parser.epsilon(42);
    assertEquals(42, ((EpsilonParser) parser).getValue());
    assertEquals("EpsilonParser[42]", parser.toString());

    assertSuccess(parser, "", 42, 0);
    assertSuccess(parser, "a", 42, 0);
  }

  @Test
  public void testEpsilonFastParse() {
    Parser parser = Parser.epsilon("val");
    assertEquals(0, parser.fastParseOn("abc", 0));
    assertEquals(2, parser.fastParseOn("abc", 2));
  }

  @Test
  public void testEpsilonCopyAndEquality() {
    Parser parser = Parser.epsilon(42);
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser differentValue = Parser.epsilon(43);
    assertFalse(parser.isEqualTo(differentValue));

    Parser nullValue = Parser.epsilon();
    assertFalse(parser.isEqualTo(nullValue));
  }

  @Test
  public void testFailure() {
    Parser parser = Parser.failure();
    assertEquals("unable to parse", ((FailureParser) parser).getMessage());
    assertFailure(parser, "", 0, "unable to parse");
    assertFailure(parser, "a", 0, "unable to parse");

    Parser custom = Parser.failure("custom failure");
    assertEquals("custom failure", ((FailureParser) custom).getMessage());
    assertFailure(custom, "", 0, "custom failure");
    assertFailure(custom, "a", 0, "custom failure");
  }

  @Test
  public void testConstant() {
    Parser parser = CharacterParser.digit().plus().constant(42);
    assertSuccess(parser, "123", 42, 3);
    assertFailure(parser, "abc", 0, "digit expected");

    assertEquals(3, parser.fastParseOn("123", 0));
    assertEquals(-1, parser.fastParseOn("abc", 0));
  }
}
