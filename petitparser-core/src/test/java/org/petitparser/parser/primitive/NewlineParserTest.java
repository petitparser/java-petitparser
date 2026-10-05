package org.petitparser.parser.primitive;

import org.junit.Test;
import org.petitparser.parser.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests for {@link NewlineParser}.
 */
public class NewlineParserTest {

  @Test
  public void testDefault() {
    NewlineParser parser = (NewlineParser) Parser.newline();
    assertEquals("newline expected", parser.getMessage());
    assertTrue(parser.toString().contains("newline expected"));

    assertSuccess(parser, "\n", "\n", 1);
    assertSuccess(parser, "\r\n", "\r\n", 2);
    assertSuccess(parser, "\r", "\r", 1);
    assertSuccess(parser, "\rx", "\r", 1);
    assertSuccess(parser, "\r\nx", "\r\n", 2);

    assertFailure(parser, "", 0, "newline expected");
    assertFailure(parser, "\f", 0, "newline expected");
  }

  @Test
  public void testCustomMessage() {
    Parser parser = Parser.newline("line break expected");
    assertFailure(parser, "", 0, "line break expected");
  }

  @Test
  public void testFastParse() {
    Parser parser = Parser.newline();
    assertEquals(1, parser.fastParseOn("\n", 0));
    assertEquals(2, parser.fastParseOn("\r\n", 0));
    assertEquals(1, parser.fastParseOn("\r", 0));
    assertEquals(1, parser.fastParseOn("\rx", 0));
    assertEquals(2, parser.fastParseOn("\r\nx", 0));
    assertEquals(-1, parser.fastParseOn("a", 0));
    assertEquals(-1, parser.fastParseOn("", 0));
  }

  @Test
  public void testNullCheck() {
    try {
      new NewlineParser(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }
  }

  @Test
  public void testCopyAndEquality() {
    Parser parser = Parser.newline();
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser custom = Parser.newline("custom");
    assertFalse(parser.isEqualTo(custom));
    assertFalse(parser.isEqualTo(PositionParser.INSTANCE));
  }
}
