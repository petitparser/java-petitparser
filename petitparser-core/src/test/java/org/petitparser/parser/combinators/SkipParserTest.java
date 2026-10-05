package org.petitparser.parser.combinators;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.EpsilonParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests for {@link SkipParser}.
 */
public class SkipParserTest {

  private final Parser inner = digit();
  private final Parser before = of('<');
  private final Parser after = of('>');

  @Test
  public void testSequentialMarker() {
    Parser parser = inner.skip(before, after);
    assertTrue(parser instanceof SequentialParser);
  }

  @Test
  public void testNone() {
    SkipParser parser = new SkipParser(inner);
    assertTrue(parser.getBefore() instanceof EpsilonParser);
    assertTrue(parser.getAfter() instanceof EpsilonParser);
    assertSame(inner, parser.getChildren().get(1));

    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "2", '2');
    assertFailure(parser, "", 0, "digit expected");
  }

  @Test
  public void testBefore() {
    SkipParser parser = (SkipParser) inner.skip(before, null);
    assertSame(before, parser.getBefore());
    assertTrue(parser.getAfter() instanceof EpsilonParser);

    assertSuccess(parser, "<1", '1');
    assertSuccess(parser, "<2", '2');
    assertFailure(parser, "", 0, "'<' expected");
    assertFailure(parser, "1", 0, "'<' expected");
    assertFailure(parser, "<", 1, "digit expected");
    assertFailure(parser, "<a", 1, "digit expected");
  }

  @Test
  public void testAfter() {
    SkipParser parser = (SkipParser) inner.skip(null, after);
    assertTrue(parser.getBefore() instanceof EpsilonParser);
    assertSame(after, parser.getAfter());

    assertSuccess(parser, "1>", '1');
    assertSuccess(parser, "2>", '2');
    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "1", 1, "'>' expected");
    assertFailure(parser, "1!", 1, "'>' expected");
    assertFailure(parser, ">", 0, "digit expected");
    assertFailure(parser, "a>", 0, "digit expected");
  }

  @Test
  public void testBeforeAndAfter() {
    SkipParser parser = (SkipParser) inner.skip(before, after);
    assertSame(before, parser.getBefore());
    assertSame(after, parser.getAfter());

    assertSuccess(parser, "<1>", '1');
    assertSuccess(parser, "<2>", '2');
    assertFailure(parser, "", 0, "'<' expected");
    assertFailure(parser, "1", 0, "'<' expected");
    assertFailure(parser, "1>", 0, "'<' expected");
    assertFailure(parser, "1!", 0, "'<' expected");
    assertFailure(parser, "<", 1, "digit expected");
    assertFailure(parser, "<1", 2, "'>' expected");
    assertFailure(parser, "<1!", 2, "'>' expected");
  }

  @Test
  public void testFastParse() {
    Parser parser = inner.skip(before, after);
    assertEquals(3, parser.fastParseOn("<1>", 0));
    assertEquals(-1, parser.fastParseOn("<1", 0));
    assertEquals(-1, parser.fastParseOn("1>", 0));
    assertEquals(-1, parser.fastParseOn("<a", 0));
  }

  @Test
  public void testNullDelegate() {
    try {
      new SkipParser(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }
  }

  @Test
  public void testCopyAndEquality() {
    Parser parser = inner.skip(before, after);
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser differentInner = CharacterParser.letter().skip(before, after);
    assertFalse(parser.isEqualTo(differentInner));

    Parser differentBefore = inner.skip(CharacterParser.of('['), after);
    assertFalse(parser.isEqualTo(differentBefore));

    Parser differentAfter = inner.skip(before, CharacterParser.of(']'));
    assertFalse(parser.isEqualTo(differentAfter));
  }

  @Test
  public void testReplace() {
    SkipParser parser = (SkipParser) inner.skip(before, after);
    Parser newBefore = CharacterParser.of('[');
    Parser newAfter = CharacterParser.of(']');
    Parser newInner = CharacterParser.letter();

    parser.replace(before, newBefore);
    assertSame(newBefore, parser.getBefore());

    parser.replace(after, newAfter);
    assertSame(newAfter, parser.getAfter());

    parser.replace(inner, newInner);
    assertSame(newInner, parser.getChildren().get(1));

    parser.replace(CharacterParser.of('z'), CharacterParser.of('y'));
    assertSame(newBefore, parser.getBefore());
    assertSame(newAfter, parser.getAfter());
    assertSame(newInner, parser.getChildren().get(1));
  }
}
