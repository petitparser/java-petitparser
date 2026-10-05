package org.petitparser.parser.primitive;

import org.junit.Test;
import org.petitparser.parser.Parser;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests for {@link PositionParser}.
 */
public class PositionParserTest {

  @Test
  public void testPosition() {
    Parser parser = Parser.position();
    assertSame(PositionParser.INSTANCE, parser);
    assertSuccess(parser, "", 0, 0);
    assertSuccess(parser, "a", 0, 0);
  }

  @Test
  public void testPositionInSequence() {
    Parser parser = CharacterParser.of('a')
        .seq(Parser.position())
        .seq(CharacterParser.of('b'));
    assertSuccess(parser, "ab", Arrays.asList('a', 1, 'b'), 2);
  }

  @Test
  public void testFastParse() {
    Parser parser = PositionParser.INSTANCE;
    assertEquals(0, parser.fastParseOn("abc", 0));
    assertEquals(2, parser.fastParseOn("abc", 2));
  }

  @Test
  public void testCopyAndEquality() {
    Parser parser = PositionParser.INSTANCE;
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));
    assertFalse(parser.isEqualTo(new EpsilonParser()));
  }
}
