package org.petitparser;

import static org.petitparser.ParserAssertions.assertFail;
import static org.petitparser.ParserAssertions.assertParse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests {@link Parser} and {@link Parsers} and all implementing classes.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ParserTests {

  @Test
  public void testAnd() {
    Parser<List<String>> parser = string("foo").seq(string("bar"));
    assertParse(parser, "foobar", new ArrayList("foo", "bar"), 3);
    assertFail(parser, "foobaz");
  }

}
