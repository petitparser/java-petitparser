package org.petitparser;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Parser} parsing.
 */
public class ParsingTest {

  @Test
  public void testParse() {
    Parser parser = CharacterParser.is('a');
    assertTrue(parser.parse("a").isSuccess());
    assertFalse(parser.parse("b").isSuccess());
  }

  @Test
  public void testAccepts() {
    Parser parser = CharacterParser.is('a');
    assertTrue(parser.accept("a"));
    assertFalse(parser.accept("b"));
  }

  @Test
  public void testMatches() {
    Parser parser = CharacterParser.digit().seq(CharacterParser.digit()).flatten();
    List<String> expected = Arrays.asList("12", "23", "45");
    List<String> actual = parser.matches("a123b45");
    assertEquals(expected, actual);
  }

  @Test
  public void testMatchesSkipping() {
    Parser parser = CharacterParser.digit().seq(CharacterParser.digit()).flatten();
    List<String> expected = Arrays.asList("12", "45");
    List<String> actual = parser.matchesSkipping("a123b45");
    assertEquals(expected, actual);
  }
}
