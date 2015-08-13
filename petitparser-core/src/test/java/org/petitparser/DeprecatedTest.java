package org.petitparser;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Smoke test for the deprecated factory methods.
 */
@Deprecated
public class DeprecatedTest {

  @Test
  public void testAny() throws Exception {
    assertNotNull(Chars.any());
  }

  @Test
  public void testAny1() throws Exception {
    assertNotNull(Chars.any("Message"));
  }

  @Test
  public void testAnyOf() throws Exception {
    assertNotNull(Chars.anyOf("abc"));
  }

  @Test
  public void testAnyOf1() throws Exception {
    assertNotNull(Chars.anyOf("abc", "Message"));
  }

  @Test
  public void testCharacter() throws Exception {
    assertNotNull(Chars.character('a'));
  }

  @Test
  public void testCharacter1() throws Exception {
    assertNotNull(Chars.character('a', "Message"));
  }

  @Test
  public void testDigit() throws Exception {
    assertNotNull(Chars.digit());
  }

  @Test
  public void testDigit1() throws Exception {
    assertNotNull(Chars.digit("Message"));
  }

  @Test
  public void testLetter() throws Exception {
    assertNotNull(Chars.letter());
  }

  @Test
  public void testLetter1() throws Exception {
    assertNotNull(Chars.letter("Message"));
  }

  @Test
  public void testLowerCase() throws Exception {
    assertNotNull(Chars.lowerCase());
  }

  @Test
  public void testLowerCase1() throws Exception {
    assertNotNull(Chars.lowerCase("Message"));
  }

  @Test
  public void testPattern() throws Exception {
    assertNotNull(Chars.pattern("a"));
  }

  @Test
  public void testPattern1() throws Exception {
    assertNotNull(Chars.pattern("a", "Message"));
  }

  @Test
  public void testRange() throws Exception {
    assertNotNull(Chars.range('a', 'b'));
  }

  @Test
  public void testRange1() throws Exception {
    assertNotNull(Chars.range('a', 'b', "Message"));
  }

  @Test
  public void testUpperCase() throws Exception {
    assertNotNull(Chars.upperCase());
  }

  @Test
  public void testUpperCase1() throws Exception {
    assertNotNull(Chars.upperCase("Message"));
  }

  @Test
  public void testWhitespace() throws Exception {
    assertNotNull(Chars.whitespace());
  }

  @Test
  public void testWhitespace1() throws Exception {
    assertNotNull(Chars.whitespace("Message"));
  }

  @Test
  public void testWord() throws Exception {
    assertNotNull(Chars.word());
  }

  @Test
  public void testWord1() throws Exception {
    assertNotNull(Chars.word("Message"));
  }

  @Test
  public void testEpsilon() throws Exception {
    assertNotNull(Parsers.epsilon());
  }

  @Test
  public void testFailure() throws Exception {
    assertNotNull(Parsers.failure("Message"));
  }

  @Test
  public void testString() throws Exception {
    assertNotNull(Parsers.string("abc"));
  }

  @Test
  public void testString1() throws Exception {
    assertNotNull(Parsers.string("abc", "Message"));
  }

  @Test
  public void testStringIgnoreCase() throws Exception {
    assertNotNull(Parsers.stringIgnoreCase("abc"));
  }

  @Test
  public void testStringIgnoreCase1() throws Exception {
    assertNotNull(Parsers.stringIgnoreCase("abc", "Message"));
  }

  @Test
  public void testParse() throws Exception {
    Parsing.parse(Chars.character('a'), "a");
  }

  @Test
  public void testAccepts() throws Exception {
    Parsing.accepts(Chars.character('a'), "a");
  }

  @Test
  public void testMatches() throws Exception {
    Parsing.matches(Chars.character('a'), "a");
  }

  @Test
  public void testMatchesSkipping() throws Exception {
    Parsing.matchesSkipping(Chars.character('a'), "a");
  }

}
