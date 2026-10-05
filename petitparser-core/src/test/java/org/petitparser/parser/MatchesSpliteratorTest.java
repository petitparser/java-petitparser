package org.petitparser.parser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests {@link MatchesSpliterator}.
 */
public class MatchesSpliteratorTest {

  @Test
  public void testCharacteristicsAndProperties() {
    Parser parser = digit();
    MatchesSpliterator<String> spliterator =
        new MatchesSpliterator<>(parser, "123", 0, true);

    assertEquals(parser, spliterator.getParser());
    assertEquals("123", spliterator.getInput());
    assertEquals(0, spliterator.getStart());
    assertEquals(0, spliterator.getCurrent());
    assertTrue(spliterator.isOverlapping());
    assertNull(spliterator.trySplit());
    assertEquals(Long.MAX_VALUE, spliterator.estimateSize());
    assertEquals(Spliterator.ORDERED, spliterator.characteristics());
    assertTrue(spliterator.toString().contains("MatchesSpliterator"));
  }

  @Test
  public void testOverlappingAdvance() {
    Parser parser = digit().seq(digit()).flatten();
    MatchesSpliterator<String> spliterator =
        new MatchesSpliterator<>(parser, "1234", 0, true);

    List<String> results = new ArrayList<>();
    while (spliterator.tryAdvance(results::add)) {
      // Loop until false
    }
    assertEquals(Arrays.asList("12", "23", "34"), results);
    assertFalse(spliterator.tryAdvance(results::add));
  }

  @Test
  public void testNonOverlappingForEachRemaining() {
    Parser parser = digit().seq(digit()).flatten();
    MatchesSpliterator<String> spliterator =
        new MatchesSpliterator<>(parser, "1234", 0, false);

    List<String> results = new ArrayList<>();
    spliterator.forEachRemaining(results::add);
    assertEquals(Arrays.asList("12", "34"), results);
  }

  @Test
  public void testZeroLengthMatchProgresses() {
    Parser parser = digit().star().flatten();
    MatchesSpliterator<String> spliterator =
        new MatchesSpliterator<>(parser, "a12b", 0, false);

    List<String> results = new ArrayList<>();
    spliterator.forEachRemaining(results::add);
    assertEquals(Arrays.asList("", "12", "", ""), results);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testNegativeStartThrows() {
    new MatchesSpliterator<>(of('a'), "abc", -1, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullParserThrows() {
    new MatchesSpliterator<>(null, "abc", 0, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullInputThrows() {
    new MatchesSpliterator<>(of('a'), null, 0, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullActionInTryAdvanceThrows() {
    MatchesSpliterator<Object> spliterator =
        new MatchesSpliterator<>(of('a'), "abc", 0, false);
    spliterator.tryAdvance(null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullActionInForEachRemainingThrows() {
    MatchesSpliterator<Object> spliterator =
        new MatchesSpliterator<>(of('a'), "abc", 0, false);
    spliterator.forEachRemaining(null);
  }
}
