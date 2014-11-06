package org.petitparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Chars.character;
import static org.petitparser.Chars.digit;
import static org.petitparser.Parsing.matches;
import static org.petitparser.Parsing.matchesSkipping;
import static org.petitparser.Parsing.parse;
import static org.petitparser.Parsing.accepts;

import java.util.List;

import org.junit.Test;
import org.petitparser.parser.Parser;

import com.google.common.collect.Lists;

/**
 * Tests {@link Parsing} utility.
 */
public class ParsingTest {

  @Test
  public void testParse() {
    Parser parser = character('a');
    assertTrue(parse(parser, "a").isSuccess());
    assertFalse(parse(parser, "b").isSuccess());
  }

  @Test
  public void testAccepts() {
    Parser parser = character('a');
    assertTrue(accepts(parser, "a"));
    assertFalse(accepts(parser, "b"));
  }

  @Test
  public void testMatches() {
    Parser parser = digit().seq(digit()).flatten();
    List<String> expected = Lists.newArrayList("12", "23", "45");
    List<String> actual = matches(parser, "a123b45");
    assertEquals(expected, actual);
  }

  @Test
  public void testMatchesSkipping() {
    Parser parser = digit().seq(digit()).flatten();
    List<String> expected = Lists.newArrayList("12", "45");
    List<String> actual = matchesSkipping(parser, "a123b45");
    assertEquals(expected, actual);
  }

}
