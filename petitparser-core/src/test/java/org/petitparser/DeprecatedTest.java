package org.petitparser;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Smoke test for the deprecated factory methods.
 */
@Deprecated
public class DeprecatedTest {

  @Test
  public void testAny() {
    assertNotNull(Chars.any());
  }

  @Test
  public void testAny1() {
    assertNotNull(Chars.any("Message"));
  }

  @Test
  public void testAnyOf() {
    assertNotNull(Chars.anyOf("abc"));
  }

  @Test
  public void testAnyOf1() {
    assertNotNull(Chars.anyOf("abc", "Message"));
  }

  @Test
  public void testCharacter() {
    assertNotNull(Chars.character('a'));
  }

  @Test
  public void testCharacter1() {
    assertNotNull(Chars.character('a', "Message"));
  }

  @Test
  public void testDigit() {
    assertNotNull(Chars.digit());
  }

  @Test
  public void testDigit1() {
    assertNotNull(Chars.digit("Message"));
  }

  @Test
  public void testLetter() {
    assertNotNull(Chars.letter());
  }

  @Test
  public void testLetter1() {
    assertNotNull(Chars.letter("Message"));
  }

  @Test
  public void testLowerCase() {
    assertNotNull(Chars.lowerCase());
  }

  @Test
  public void testLowerCase1() {
    assertNotNull(Chars.lowerCase("Message"));
  }

  @Test
  public void testPattern() {
    assertNotNull(Chars.pattern("a"));
  }

  @Test
  public void testPattern1() {
    assertNotNull(Chars.pattern("a", "Message"));
  }

  @Test
  public void testRange() {
    assertNotNull(Chars.range('a', 'b'));
  }

  @Test
  public void testRange1() {
    assertNotNull(Chars.range('a', 'b', "Message"));
  }

  @Test
  public void testUpperCase() {
    assertNotNull(Chars.upperCase());
  }

  @Test
  public void testUpperCase1() {
    assertNotNull(Chars.upperCase("Message"));
  }

  @Test
  public void testWhitespace() {
    assertNotNull(Chars.whitespace());
  }

  @Test
  public void testWhitespace1() {
    assertNotNull(Chars.whitespace("Message"));
  }

  @Test
  public void testWord() {
    assertNotNull(Chars.word());
  }

  @Test
  public void testWord1() {
    assertNotNull(Chars.word("Message"));
  }

  @Test
  public void testEpsilon() {
    assertNotNull(Parsers.epsilon());
  }

  @Test
  public void testFailure() {
    assertNotNull(Parsers.failure("Message"));
  }

  @Test
  public void testString() {
    assertNotNull(Parsers.string("abc"));
  }

  @Test
  public void testString1() {
    assertNotNull(Parsers.string("abc", "Message"));
  }

  @Test
  public void testStringIgnoreCase() {
    assertNotNull(Parsers.stringIgnoreCase("abc"));
  }

  @Test
  public void testStringIgnoreCase1() {
    assertNotNull(Parsers.stringIgnoreCase("abc", "Message"));
  }

  @Test
  public void testParse() {
    Parsing.parse(Chars.character('a'), "a");
  }

  @Test
  public void testAccepts() {
    Parsing.accepts(Chars.character('a'), "a");
  }

  @Test
  public void testMatches() {
    Parsing.matches(Chars.character('a'), "a");
  }

  @Test
  public void testMatchesSkipping() {
    Parsing.matchesSkipping(Chars.character('a'), "a");
  }

}
