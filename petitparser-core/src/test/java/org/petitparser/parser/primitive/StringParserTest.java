package org.petitparser.parser.primitive;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link StringParser} and {@link StringIgnoreCaseParser}.
 */
public class StringParserTest {

  @Test
  public void testStringParserSuccess() {
    Parser parser = StringParser.of("petit");
    Result result = parser.parse("petitparser");
    assertTrue(result.isSuccess());
    assertEquals("petit", result.get());
    assertEquals(5, result.getPosition());
  }

  @Test
  public void testStringParserFailure() {
    Parser parser = StringParser.of("petit");
    Result result = parser.parse("large");
    assertTrue(result.isFailure());
    assertEquals("petit expected", result.getMessage());
    assertEquals(0, result.getPosition());
  }

  @Test
  public void testStringParserCustomMessage() {
    Parser parser = StringParser.of("petit", "expected petit");
    Result result = parser.parse("large");
    assertTrue(result.isFailure());
    assertEquals("expected petit", result.getMessage());
  }

  @Test
  public void testStringParserFastParse() {
    Parser parser = StringParser.of("petit");
    assertEquals(5, parser.fastParseOn("petitparser", 0));
    assertEquals(8, parser.fastParseOn("abcpetitxyz", 3));
    assertEquals(-1, parser.fastParseOn("large", 0));
    assertEquals(-1, parser.fastParseOn("pet", 0));
  }

  @Test
  public void testStringParserEquality() {
    Parser p1 = StringParser.of("petit");
    Parser p2 = StringParser.of("petit");
    Parser p3 = StringParser.of("other");
    Parser p4 = StringParser.of("petit", "different message");

    assertTrue(p1.isEqualTo(p2));
    assertFalse(p1.isEqualTo(p3));
    assertFalse(p1.isEqualTo(p4));
    assertEquals(p1.toString(), p2.toString());
  }

  @Test
  public void testStringIgnoreCaseParserSuccess() {
    Parser parser = StringParser.ofIgnoringCase("petit");
    Result r1 = parser.parse("petit");
    assertTrue(r1.isSuccess());
    assertEquals("petit", r1.get());
    assertEquals(5, r1.getPosition());

    Result r2 = parser.parse("PETIT");
    assertTrue(r2.isSuccess());
    assertEquals("PETIT", r2.get());
    assertEquals(5, r2.getPosition());

    Result r3 = parser.parse("PeTiT");
    assertTrue(r3.isSuccess());
    assertEquals("PeTiT", r3.get());
    assertEquals(5, r3.getPosition());
  }

  @Test
  public void testStringIgnoreCaseParserFailure() {
    Parser parser = StringParser.ofIgnoringCase("petit");
    Result result = parser.parse("large");
    assertTrue(result.isFailure());
    assertEquals("petit expected", result.getMessage());
    assertEquals(0, result.getPosition());
  }

  @Test
  public void testStringIgnoreCaseParserFastParse() {
    Parser parser = StringParser.ofIgnoringCase("petit");
    assertEquals(5, parser.fastParseOn("petitparser", 0));
    assertEquals(5, parser.fastParseOn("PETITPARSER", 0));
    assertEquals(8, parser.fastParseOn("abcPETITxyz", 3));
    assertEquals(-1, parser.fastParseOn("large", 0));
    assertEquals(-1, parser.fastParseOn("pet", 0));
  }

  @Test
  public void testStringIgnoreCaseParserEquality() {
    Parser p1 = StringIgnoreCaseParser.of("petit", "expected");
    Parser p2 = StringIgnoreCaseParser.of("PETIT", "expected");
    Parser p3 = StringIgnoreCaseParser.of("other", "expected");
    Parser p4 = StringParser.of("petit", "expected");

    assertTrue(p1.isEqualTo(p2));
    assertFalse(p1.isEqualTo(p3));
    assertFalse(p1.isEqualTo(p4));
    assertEquals("petit", ((StringIgnoreCaseParser) p1).getValue());
    assertEquals("expected", ((StringIgnoreCaseParser) p1).getMessage());
    assertTrue(p1.toString().contains("expected"));
  }

  @Test
  public void testCopy() {
    Parser p1 = StringParser.of("test");
    Parser c1 = p1.copy();
    assertTrue(p1.isEqualTo(c1));

    Parser p2 = StringIgnoreCaseParser.of("test");
    Parser c2 = p2.copy();
    assertTrue(p2.isEqualTo(c2));
  }
}
