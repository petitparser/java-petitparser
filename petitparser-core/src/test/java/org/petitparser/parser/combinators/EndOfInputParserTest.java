package org.petitparser.parser.combinators;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link EndOfInputParser}.
 */
public class EndOfInputParserTest {

  @Test
  public void testParseOn() {
    EndOfInputParser parser = new EndOfInputParser("end expected");
    Result r1 = parser.parseOn(new Context("", 0));
    assertTrue(r1.isSuccess());
    assertNull(r1.get());
    assertEquals(0, r1.getPosition());

    Result r2 = parser.parseOn(new Context("abc", 3));
    assertTrue(r2.isSuccess());
    assertNull(r2.get());
    assertEquals(3, r2.getPosition());

    Result r3 = parser.parseOn(new Context("abc", 0));
    assertTrue(r3.isFailure());
    assertEquals("end expected", r3.getMessage());
    assertEquals(0, r3.getPosition());

    Result r4 = parser.parseOn(new Context("abc", 2));
    assertTrue(r4.isFailure());
    assertEquals("end expected", r4.getMessage());
    assertEquals(2, r4.getPosition());
  }

  @Test
  public void testFastParseOn() {
    EndOfInputParser parser = new EndOfInputParser("end expected");
    assertEquals(0, parser.fastParseOn("", 0));
    assertEquals(3, parser.fastParseOn("abc", 3));
    assertEquals(-1, parser.fastParseOn("abc", 0));
    assertEquals(-1, parser.fastParseOn("abc", 1));
  }

  @Test(expected = NullPointerException.class)
  public void testNullMessage() {
    new EndOfInputParser(null);
  }

  @Test
  public void testCopyAndEquals() {
    EndOfInputParser p1 = new EndOfInputParser("end expected");
    EndOfInputParser p2 = new EndOfInputParser("end expected");
    EndOfInputParser p3 = new EndOfInputParser("different message");

    assertTrue(p1.isEqualTo(p2));
    assertFalse(p1.isEqualTo(p3));
    assertFalse(p1.isEqualTo(CharacterParser.any()));

    assertTrue(p1.hasEqualProperties(p2));
    assertFalse(p1.hasEqualProperties(p3));

    EndOfInputParser copy = p1.copy();
    assertTrue(p1.isEqualTo(copy));
    assertEquals("end expected", copy.message);
  }

  @Test
  public void testToString() {
    EndOfInputParser parser = new EndOfInputParser("end of file");
    assertTrue(parser.toString().contains("[end of file]"));
  }
}
