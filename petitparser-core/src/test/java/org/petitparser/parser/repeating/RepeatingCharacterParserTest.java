package org.petitparser.parser.repeating;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests for {@link RepeatingCharacterParser}.
 */
public class RepeatingCharacterParserTest {

  @Test
  public void testStarString() {
    Parser parser = CharacterParser.digit().starString();
    assertTrue(parser instanceof RepeatingCharacterParser);
    RepeatingCharacterParser rcp = (RepeatingCharacterParser) parser;
    assertEquals(0, rcp.getMin());
    assertEquals(RepeatingParser.UNBOUNDED, rcp.getMax());
    assertEquals("digit expected", rcp.getMessage());
    assertSame(CharacterParser.digit().getMatcher().getClass(), rcp.getPredicate().getClass());

    assertSuccess(parser, "", "", 0);
    assertSuccess(parser, "a", "", 0);
    assertSuccess(parser, "1", "1", 1);
    assertSuccess(parser, "123", "123", 3);
    assertSuccess(parser, "123a", "123", 3);
  }

  @Test
  public void testStarStringWithMessage() {
    Parser parser = CharacterParser.digit().starString("custom message");
    assertTrue(parser instanceof RepeatingCharacterParser);
    assertEquals("custom message", ((RepeatingCharacterParser) parser).getMessage());
    assertSuccess(parser, "123", "123", 3);
  }

  @Test
  public void testPlusString() {
    Parser parser = CharacterParser.digit().plusString();
    assertTrue(parser instanceof RepeatingCharacterParser);
    RepeatingCharacterParser rcp = (RepeatingCharacterParser) parser;
    assertEquals(1, rcp.getMin());
    assertEquals(RepeatingParser.UNBOUNDED, rcp.getMax());

    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "a", 0, "digit expected");
    assertSuccess(parser, "1", "1", 1);
    assertSuccess(parser, "12", "12", 2);
    assertSuccess(parser, "123b", "123", 3);
  }

  @Test
  public void testPlusStringWithMessage() {
    Parser parser = CharacterParser.digit().plusString("digits expected");
    assertTrue(parser instanceof RepeatingCharacterParser);
    assertFailure(parser, "", 0, "digits expected");
    assertFailure(parser, "a", 0, "digits expected");
    assertSuccess(parser, "1", "1", 1);
  }

  @Test
  public void testTimesString() {
    Parser parser = CharacterParser.digit().timesString(3);
    assertTrue(parser instanceof RepeatingCharacterParser);
    RepeatingCharacterParser rcp = (RepeatingCharacterParser) parser;
    assertEquals(3, rcp.getMin());
    assertEquals(3, rcp.getMax());

    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "1", 1, "digit expected");
    assertFailure(parser, "12", 2, "digit expected");
    assertSuccess(parser, "123", "123", 3);
    assertSuccess(parser, "1234", "123", 3);
  }

  @Test
  public void testTimesStringWithMessage() {
    Parser parser = CharacterParser.digit().timesString(2, "two digits");
    assertTrue(parser instanceof RepeatingCharacterParser);
    assertFailure(parser, "1", 1, "two digits");
    assertSuccess(parser, "12", "12", 2);
  }

  @Test
  public void testRepeatString() {
    Parser parser = CharacterParser.digit().repeatString(2, 4);
    assertTrue(parser instanceof RepeatingCharacterParser);
    RepeatingCharacterParser rcp = (RepeatingCharacterParser) parser;
    assertEquals(2, rcp.getMin());
    assertEquals(4, rcp.getMax());

    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "1", 1, "digit expected");
    assertSuccess(parser, "12", "12", 2);
    assertSuccess(parser, "123", "123", 3);
    assertSuccess(parser, "1234", "1234", 4);
    assertSuccess(parser, "12345", "1234", 4);
  }

  @Test
  public void testRepeatStringWithMessage() {
    Parser parser = CharacterParser.digit().repeatString(2, 3, "expected 2-3 digits");
    assertTrue(parser instanceof RepeatingCharacterParser);
    assertFailure(parser, "1", 1, "expected 2-3 digits");
    assertSuccess(parser, "12", "12", 2);
  }

  @Test
  public void testGeneralParserRepeatString() {
    Parser parser = StringParser.of("ab").plusString();
    assertFalse(parser instanceof RepeatingCharacterParser);

    assertFailure(parser, "", 0);
    assertFailure(parser, "a", 0);
    assertSuccess(parser, "ab", "ab", 2);
    assertSuccess(parser, "abab", "abab", 4);
    assertSuccess(parser, "ababc", "abab", 4);

    Parser parserWithMsg = StringParser.of("ab").plusString("ab expected");
    assertFailure(parserWithMsg, "x", 0, "ab expected");

    Parser starParser = StringParser.of("ab").starString();
    assertSuccess(starParser, "", "", 0);
    assertSuccess(starParser, "ab", "ab", 2);

    Parser starParserWithMsg = StringParser.of("ab").starString("custom");
    assertSuccess(starParserWithMsg, "ab", "ab", 2);

    Parser timesParser = StringParser.of("ab").timesString(2);
    assertFailure(timesParser, "ab", 2);
    assertSuccess(timesParser, "abab", "abab", 4);

    Parser timesParserWithMsg = StringParser.of("ab").timesString(2, "two abs");
    assertFailure(timesParserWithMsg, "ab", 0, "two abs");
  }

  @Test
  public void testFastParseOnOffset() {
    Parser parser = CharacterParser.digit().repeatString(2, 4);
    assertEquals(-1, parser.fastParseOn("a12b", 0));
    assertEquals(3, parser.fastParseOn("a12b", 1));
    assertEquals(5, parser.fastParseOn("a12345b", 1));
  }

  @Test
  public void testParseOnOffset() {
    Parser parser = CharacterParser.digit().repeatString(2, 4);
    org.petitparser.context.Result r1 = parser.parseOn(new org.petitparser.context.Context("a12b", 1));
    assertTrue(r1.isSuccess());
    assertEquals("12", r1.get());
    assertEquals(3, r1.getPosition());

    org.petitparser.context.Result r2 = parser.parseOn(new org.petitparser.context.Context("a12345b", 1));
    assertTrue(r2.isSuccess());
    assertEquals("1234", r2.get());
    assertEquals(5, r2.getPosition());

    org.petitparser.context.Result r3 = parser.parseOn(new org.petitparser.context.Context("a1b", 1));
    assertTrue(r3.isFailure());
    assertEquals(2, r3.getPosition());
    assertEquals("digit expected", r3.getMessage());
  }

  @Test
  public void testCopyAndEquality() {
    RepeatingCharacterParser parser = CharacterParser.digit().plusString();
    RepeatingCharacterParser copy = parser.copy();

    assertNotSame(parser, copy);
    assertEquals(parser.getMin(), copy.getMin());
    assertEquals(parser.getMax(), copy.getMax());
    assertEquals(parser.getMessage(), copy.getMessage());
    assertSame(parser.getPredicate(), copy.getPredicate());

    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    assertFalse(parser.isEqualTo(CharacterParser.digit().starString()));
    assertFalse(parser.isEqualTo(CharacterParser.letter().plusString()));
    assertFalse(parser.isEqualTo(CharacterParser.digit().plusString("other")));
  }

  @Test
  public void testToString() {
    Parser parser = CharacterParser.digit().plusString();
    assertEquals("RepeatingCharacterParser[digit expected, 1..*]", parser.toString());

    Parser bounded = CharacterParser.digit().repeatString(2, 4);
    assertEquals("RepeatingCharacterParser[digit expected, 2..4]", bounded.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeMin() {
    new RepeatingCharacterParser(Character::isDigit, "msg", -1, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMinGreaterThanMax() {
    new RepeatingCharacterParser(Character::isDigit, "msg", 5, 2);
  }

  @Test(expected = NullPointerException.class)
  public void testNullPredicate() {
    new RepeatingCharacterParser(null, "msg", 1, 2);
  }

  @Test(expected = NullPointerException.class)
  public void testNullMessage() {
    new RepeatingCharacterParser(Character::isDigit, null, 1, 2);
  }

  @Test
  public void testCharacterParserGetters() {
    CharacterParser parser = CharacterParser.digit();
    assertEquals("digit expected", parser.getMessage());
    assertTrue(parser.getMatcher().test('5'));
    assertFalse(parser.getMatcher().test('a'));
  }

  @Test
  public void testUnicodeSurrogateStarString() {
    // Rocket emoji: \uD83D\uDE80 (code point 0x1F680)
    Parser parser = CharacterParser.of(0x1F680, true).starString();
    assertTrue(parser instanceof RepeatingCharacterParser);
    assertTrue(((RepeatingCharacterParser) parser).isUnicode());

    assertSuccess(parser, "", "", 0);
    assertSuccess(parser, "\uD83D\uDE80", "\uD83D\uDE80", 2);
    assertSuccess(parser, "\uD83D\uDE80\uD83D\uDE80", "\uD83D\uDE80\uD83D\uDE80", 4);
    assertSuccess(parser, "\uD83D\uDE80abc", "\uD83D\uDE80", 2);
    assertSuccess(parser, "abc", "", 0);

    // Fast-parse verification
    assertTrue(parser.accept(""));
    assertTrue(parser.accept("\uD83D\uDE80\uD83D\uDE80"));
    assertEquals(4, parser.fastParseOn("\uD83D\uDE80\uD83D\uDE80xyz", 0));
    assertEquals(0, parser.fastParseOn("xyz", 0));

    // Isolated surrogate (not rocket)
    assertSuccess(parser, "\uD83D", "", 0);
    assertEquals(0, parser.fastParseOn("\uD83D", 0));
  }

  @Test
  public void testUnicodeSurrogatePlusString() {
    Parser parser = CharacterParser.of(0x1F680, true).plusString();
    assertTrue(parser instanceof RepeatingCharacterParser);
    assertTrue(((RepeatingCharacterParser) parser).isUnicode());

    assertFailure(parser, "", 0);
    assertFailure(parser, "abc", 0);
    assertFailure(parser, "\uD83D", 0); // incomplete surrogate
    assertFailure(parser, "\uD83D\uDE00", 0); // grinning face (different surrogate pair)

    assertSuccess(parser, "\uD83D\uDE80", "\uD83D\uDE80", 2);
    assertSuccess(parser, "\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80", "\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80", 6);

    // Fast-parse verification
    assertFalse(parser.accept(""));
    assertFalse(parser.accept("abc"));
    assertFalse(parser.accept("\uD83D"));
    assertTrue(parser.accept("\uD83D\uDE80"));
    assertEquals(2, parser.fastParseOn("\uD83D\uDE80", 0));
    assertEquals(-1, parser.fastParseOn("xyz", 0));
    assertEquals(-1, parser.fastParseOn("\uD83D", 0));
  }

  @Test
  public void testUnicodeSurrogateTimesString() {
    Parser parser = CharacterParser.of(0x1F680, true).timesString(2);
    assertTrue(parser instanceof RepeatingCharacterParser);

    assertFailure(parser, "", 0);
    assertFailure(parser, "\uD83D\uDE80", 2);
    assertFailure(parser, "\uD83D\uDE80\uD83D", 2); // incomplete second surrogate

    assertSuccess(parser, "\uD83D\uDE80\uD83D\uDE80", "\uD83D\uDE80\uD83D\uDE80", 4);
    assertSuccess(parser, "\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80", "\uD83D\uDE80\uD83D\uDE80", 4);

    // Fast parse verification
    assertEquals(-1, parser.fastParseOn("", 0));
    assertEquals(-1, parser.fastParseOn("\uD83D\uDE80", 0));
    assertEquals(-1, parser.fastParseOn("\uD83D\uDE80\uD83D", 0));
    assertEquals(4, parser.fastParseOn("\uD83D\uDE80\uD83D\uDE80", 0));
    assertEquals(4, parser.fastParseOn("\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80", 0));
  }
}
