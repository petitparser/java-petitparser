package org.petitparser;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;

import java.util.List;

import static org.junit.Assert.*;
import static org.petitparser.Parsing.*;

/**
 * Tests {@link Parsing} utility.
 */
public class ParsingTest {

  @Test
  public void testParse() {
    Parser parser = CharacterParser.is('a');
    assertTrue(parse(parser, "a").isSuccess());
    assertFalse(parse(parser, "b").isSuccess());
  }

  @Test
  public void testAccepts() {
    Parser parser = CharacterParser.is('a');
    assertTrue(accepts(parser, "a"));
    assertFalse(accepts(parser, "b"));
  }

  @Test
  public void testMatches() {
    Parser parser = CharacterParser.digit().seq(CharacterParser.digit()).flatten();
    List<String> expected = Lists.newArrayList("12", "23", "45");
    List<String> actual = matches(parser, "a123b45");
    assertEquals(expected, actual);
  }

  @Test
  public void testMatchesSkipping() {
    Parser parser = CharacterParser.digit().seq(CharacterParser.digit()).flatten();
    List<String> expected = Lists.newArrayList("12", "45");
    List<String> actual = matchesSkipping(parser, "a123b45");
    assertEquals(expected, actual);
  }

}
