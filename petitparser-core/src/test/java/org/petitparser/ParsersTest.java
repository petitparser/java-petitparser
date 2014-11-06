package org.petitparser;

import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.Parsers.epsilon;
import static org.petitparser.Parsers.failure;
import static org.petitparser.Parsers.string;
import static org.petitparser.Parsers.stringIgnoreCase;

import org.junit.Test;
import org.petitparser.parser.Parser;

/**
 * Tests {@link Parsers} factory.
 */
public class ParsersTest {

  @Test
  public void testEpsilon() {
    Parser parser = epsilon();
    assertSuccess(parser, "", null);
    assertSuccess(parser, "a", null, 0);
  }

  @Test
  public void testFailure() {
    Parser parser = failure("failure");
    assertFailure(parser, "");
    assertFailure(parser, "a");
  }

  @Test
  public void testString() {
    Parser parser = string("foo");
    assertSuccess(parser, "foo", "foo");
    assertFailure(parser, "");
    assertFailure(parser, "f");
    assertFailure(parser, "fo");
    assertFailure(parser, "Foo");
  }

  @Test
  public void testStringIgnoreCase() {
    Parser parser = stringIgnoreCase("foo");
    assertSuccess(parser, "foo", "foo");
    assertSuccess(parser, "FOO", "FOO");
    assertSuccess(parser, "fOo", "fOo");
    assertFailure(parser, "");
    assertFailure(parser, "f");
    assertFailure(parser, "Fo");
  }

}
