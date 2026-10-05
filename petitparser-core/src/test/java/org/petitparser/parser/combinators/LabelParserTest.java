package org.petitparser.parser.combinators;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;

/**
 * Tests for {@link LabelParser}.
 */
public class LabelParserTest {

  @Test
  public void testLabel() {
    LabelParser parser = (LabelParser) digit().labeled("number");
    assertEquals("number", parser.getLabel());
    assertEquals(digit().toString() + "[number]", parser.toString());

    assertSuccess(parser, "1", '1');
    assertFailure(parser, "a", 0, "digit expected");
  }

  @Test
  public void testFastParse() {
    Parser parser = digit().labeled("number");
    assertEquals(1, parser.fastParseOn("1", 0));
    assertEquals(-1, parser.fastParseOn("a", 0));
  }

  @Test
  public void testNullChecks() {
    try {
      new LabelParser(null, "label");
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }

    try {
      new LabelParser(digit(), null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }
  }

  @Test
  public void testCopyAndEquality() {
    Parser parser = digit().labeled("number");
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser differentLabel = digit().labeled("digit");
    assertFalse(parser.isEqualTo(differentLabel));

    Parser differentDelegate = letter().labeled("number");
    assertFalse(parser.isEqualTo(differentDelegate));
  }

  @Test
  public void testReplace() {
    Parser inner = digit();
    LabelParser parser = (LabelParser) inner.labeled("number");
    Parser newDelegate = letter();

    parser.replace(inner, newDelegate);
    assertSame(newDelegate, parser.getChildren().get(0));

    parser.replace(inner, CharacterParser.any());
    assertSame(newDelegate, parser.getChildren().get(0));
  }
}
