package org.petitparser.parser.combinators;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.actions.TrimmingParser;
import org.petitparser.parser.primitive.CharacterParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests for {@link SettableParser}, {@link ResolvableParser}, and {@link SequentialParser}.
 */
public class SettableParserTest {

  @Test
  public void testSettable() {
    SettableParser parser = SettableParser.undefined();
    assertTrue(parser instanceof ResolvableParser);
    assertFailure(parser, "a", 0, "Undefined parser");

    CharacterParser delegate = CharacterParser.of('a');
    parser.set(delegate);
    assertSame(delegate, parser.get());
    assertSame(delegate, parser.resolve());
    assertSuccess(parser, "a", 'a');
  }

  @Test
  public void testSequentialMarker() {
    Parser sequence = CharacterParser.of('a').seq(CharacterParser.of('b'));
    assertTrue(sequence instanceof SequentialParser);

    Parser trimming = CharacterParser.of('a').trim();
    assertTrue(trimming instanceof SequentialParser);
  }
}
