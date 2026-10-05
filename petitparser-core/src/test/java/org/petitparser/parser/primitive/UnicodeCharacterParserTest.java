package org.petitparser.parser.primitive;

import org.junit.Test;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link UnicodeCharacterParser} and Unicode code point support.
 */
public class UnicodeCharacterParserTest {

  private static final String ROCKET = "\uD83D\uDE80";       // U+1F680
  private static final String SMILEY = "\uD83D\uDE03";       // U+1F603
  private static final String GRIN = "\uD83D\uDE01";         // U+1F601
  private static final String BEAMING = "\uD83D\uDE01";      // U+1F601
  private static final String GRINNING = "\uD83D\uDE00";     // U+1F600
  private static final String SWEAT_SMILE = "\uD83D\uDE05";  // U+1F605

  @Test
  public void testAnyUnicode() {
    Parser parser = CharacterParser.any(true);

    Result r1 = parser.parse("a");
    assertTrue(r1.isSuccess());
    assertEquals("a", r1.get());
    assertEquals(1, r1.getPosition());

    Result r2 = parser.parse(ROCKET);
    assertTrue(r2.isSuccess());
    assertEquals(ROCKET, r2.get());
    assertEquals(2, r2.getPosition());

    Result r3 = parser.parse("");
    assertTrue(r3.isFailure());
    assertEquals(0, r3.getPosition());

    assertEquals(1, parser.fastParseOn("abc", 0));
    assertEquals(2, parser.fastParseOn(ROCKET + "abc", 0));
    assertEquals(-1, parser.fastParseOn("", 0));
  }

  @Test
  public void testOfUnicode() {
    Parser parser = CharacterParser.of(0x1F680, true);

    Result r1 = parser.parse(ROCKET);
    assertTrue(r1.isSuccess());
    assertEquals(ROCKET, r1.get());
    assertEquals(2, r1.getPosition());

    Result r2 = parser.parse(SMILEY);
    assertTrue(r2.isFailure());

    Result r3 = parser.parse("a");
    assertTrue(r3.isFailure());

    assertEquals(2, parser.fastParseOn(ROCKET, 0));
    assertEquals(-1, parser.fastParseOn(SMILEY, 0));
  }

  @Test
  public void testRangeUnicode() {
    // U+1F601 (GRIN) to U+1F603 (SMILEY)
    Parser parser = CharacterParser.range(0x1F601, 0x1F603, true);

    Result r1 = parser.parse(GRIN);
    assertTrue(r1.isSuccess());
    assertEquals(GRIN, r1.get());

    Result r2 = parser.parse(SMILEY);
    assertTrue(r2.isSuccess());
    assertEquals(SMILEY, r2.get());

    Result r3 = parser.parse(GRINNING);
    assertTrue(r3.isFailure());

    Result r4 = parser.parse(SWEAT_SMILE);
    assertTrue(r4.isFailure());

    assertEquals(2, parser.fastParseOn(GRIN, 0));
    assertEquals(-1, parser.fastParseOn(GRINNING, 0));
  }

  @Test
  public void testAnyOfUnicode() {
    Parser parser = CharacterParser.anyOf("x" + ROCKET + "z", true);

    Result r1 = parser.parse("x");
    assertTrue(r1.isSuccess());
    assertEquals("x", r1.get());

    Result r2 = parser.parse(ROCKET);
    assertTrue(r2.isSuccess());
    assertEquals(ROCKET, r2.get());

    Result r3 = parser.parse("z");
    assertTrue(r3.isSuccess());
    assertEquals("z", r3.get());

    Result r4 = parser.parse("y");
    assertTrue(r4.isFailure());
  }

  @Test
  public void testNoneOfUnicode() {
    Parser parser = CharacterParser.noneOf("x" + ROCKET, true);

    Result r1 = parser.parse("y");
    assertTrue(r1.isSuccess());
    assertEquals("y", r1.get());

    Result r2 = parser.parse(SMILEY);
    assertTrue(r2.isSuccess());
    assertEquals(SMILEY, r2.get());

    Result r3 = parser.parse("x");
    assertTrue(r3.isFailure());

    Result r4 = parser.parse(ROCKET);
    assertTrue(r4.isFailure());
  }

  @Test
  public void testPatternUnicodeSingle() {
    Parser parser = CharacterParser.pattern("😮", true);
    Result r1 = parser.parse("😮");
    assertTrue(r1.isSuccess());
    assertEquals("😮", r1.get());

    Result r2 = parser.parse("😃");
    assertTrue(r2.isFailure());
    Result r3 = parser.parse("x");
    assertTrue(r3.isFailure());
  }

  @Test
  public void testPatternUnicodeRange() {
    // 😁 (0x1F601) to 😄 (0x1F604)
    Parser parser = CharacterParser.pattern("😁-😄", true);

    Result r1 = parser.parse("😁");
    assertTrue(r1.isSuccess());
    Result r2 = parser.parse("😃");
    assertTrue(r2.isSuccess());
    Result r3 = parser.parse("😄");
    assertTrue(r3.isSuccess());

    Result r4 = parser.parse("😀");
    assertTrue(r4.isFailure());
    Result r5 = parser.parse("😅");
    assertTrue(r5.isFailure());
  }

  @Test
  public void testPatternUnicodeNegated() {
    Parser parser = CharacterParser.pattern("^" + ROCKET, true);

    Result r1 = parser.parse(SMILEY);
    assertTrue(r1.isSuccess());
    assertEquals(SMILEY, r1.get());

    Result r2 = parser.parse("a");
    assertTrue(r2.isSuccess());
    assertEquals("a", r2.get());

    Result r3 = parser.parse(ROCKET);
    assertTrue(r3.isFailure());
  }

  @Test
  public void testPatternUnicodeIgnoreCase() {
    Parser parser = CharacterParser.pattern("а-в", null, true, true);

    assertTrue(parser.parse("а").isSuccess());
    assertTrue(parser.parse("б").isSuccess());
    assertTrue(parser.parse("в").isSuccess());
    assertTrue(parser.parse("А").isSuccess());
    assertTrue(parser.parse("Б").isSuccess());
    assertTrue(parser.parse("В").isSuccess());

    assertFalse(parser.parse("г").isSuccess());
    assertFalse(parser.parse("Г").isSuccess());
    assertFalse(parser.parse("a").isSuccess());
  }

  @Test
  public void testPatternUnicodeIgnoreCaseEdgeCases() {
    // Eszett 'ß' upper-cases to "SS" (length > 1 code point)
    Parser parser = CharacterParser.pattern("ß", null, true, true);
    assertTrue(parser.parse("ß").isSuccess());
    assertFalse(parser.parse("S").isSuccess());

    // Full Unicode range triggers the continue in expandCase
    String fullRangeUnicode = "\u0000-" + new String(Character.toChars(0x10ffff));
    Parser parserFull = CharacterParser.pattern(fullRangeUnicode, null, true, true);
    assertTrue(parserFull.parse("a").isSuccess());
    assertTrue(parserFull.parse(ROCKET).isSuccess());

    // Full BMP range triggers continue in non-unicode expandCase
    Parser parserBmp = CharacterParser.pattern("\u0000-\uffff", null, true, false);
    assertTrue(parserBmp.parse("a").isSuccess());
  }

  @Test
  public void testPatternAndRangeOverloads() {
    Parser p1 = CharacterParser.pattern("a-z", "custom msg", true);
    assertTrue(p1.parse("a").isSuccess());
    assertEquals("custom msg", p1.parse("1").getMessage());

    Parser r1 = CharacterParser.range('a', 'z', true);
    assertTrue(r1.parse("m").isSuccess());

    Parser r2 = CharacterParser.range('a', 'z', "range msg", true);
    assertTrue(r2.parse("m").isSuccess());
    assertEquals("range msg", r2.parse("1").getMessage());
  }

  @Test
  public void testSurrogateEdgeCases() {
    Parser parser = CharacterParser.any(true);

    // Lone high surrogate at end of buffer
    Result r1 = parser.parse("\uD83D");
    assertTrue(r1.isSuccess());
    assertEquals("\uD83D", r1.get());
    assertEquals(1, r1.getPosition());

    // High surrogate followed by non-low surrogate
    Result r2 = parser.parse("\uD83Da");
    assertTrue(r2.isSuccess());
    assertEquals("\uD83D", r2.get());
    assertEquals(1, r2.getPosition());
  }

  @Test
  public void testEqualityAndCopy() {
    Parser p1 = CharacterParser.of(0x1F680, true);
    Parser p2 = CharacterParser.of(0x1F680, true);
    Parser p3 = CharacterParser.of(0x1F603, true);
    Parser p4 = CharacterParser.of((char) 0x1F680, false);

    assertTrue(p1.isEqualTo(p2));
    assertFalse(p1.isEqualTo(p3));
    assertFalse(p1.isEqualTo(p4));

    Parser copy = p1.copy();
    assertTrue(p1.isEqualTo(copy));

    Parser neg = p1.neg("not expected");
    assertTrue(neg instanceof UnicodeCharacterParser);
    assertTrue(neg.parse(SMILEY).isSuccess());
    assertFalse(neg.parse(ROCKET).isSuccess());

    Parser negDefault = p1.neg();
    assertTrue(negDefault instanceof UnicodeCharacterParser);
    assertTrue(negDefault.parse(SMILEY).isSuccess());
    assertFalse(negDefault.parse(ROCKET).isSuccess());
  }
}
