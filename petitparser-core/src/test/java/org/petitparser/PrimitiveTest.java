package org.petitparser;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.EpsilonParser;
import org.petitparser.parser.primitive.FailureParser;
import org.petitparser.parser.primitive.StringParser;

import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests {@link EpsilonParser}, {@link FailureParser} and {@link StringParser}.
 */
public class PrimitiveTest {

  @Test
  public void testEpsilon() {
    Parser parser = EpsilonParser.DEFAULT;
    assertSuccess(parser, "", null);
    assertSuccess(parser, "a", null, 0);
  }

  @Test
  public void testFailure() {
    Parser parser = FailureParser.withMessage("wrong");
    assertFailure(parser, "", "wrong");
    assertFailure(parser, "a", "wrong");
  }

  @Test
  public void testString() {
    Parser parser = StringParser.of("foo");
    assertSuccess(parser, "foo", "foo");
    assertFailure(parser, "", "foo expected");
    assertFailure(parser, "f", "foo expected");
    assertFailure(parser, "fo", "foo expected");
    assertFailure(parser, "Foo", "foo expected");
  }

  @Test
  public void testStringWithMessage() {
    Parser parser = StringParser.of("foo", "wrong");
    assertSuccess(parser, "foo", "foo");
    assertFailure(parser, "", "wrong");
    assertFailure(parser, "f", "wrong");
    assertFailure(parser, "fo", "wrong");
    assertFailure(parser, "Foo", "wrong");
  }

  @Test
  public void testStringIgnoreCase() {
    Parser parser = StringParser.ofIgnoringCase("foo");
    assertSuccess(parser, "foo", "foo");
    assertSuccess(parser, "FOO", "FOO");
    assertSuccess(parser, "fOo", "fOo");
    assertFailure(parser, "", "foo expected");
    assertFailure(parser, "f", "foo expected");
    assertFailure(parser, "Fo", "foo expected");
  }

  @Test
  public void testStringIgnoreCaseWithMessage() {
    Parser parser = StringParser.ofIgnoringCase("foo", "wrong");
    assertSuccess(parser, "foo", "foo");
    assertSuccess(parser, "FOO", "FOO");
    assertSuccess(parser, "fOo", "fOo");
    assertFailure(parser, "", "wrong");
    assertFailure(parser, "f", "wrong");
    assertFailure(parser, "Fo", "wrong");
  }
}
