package org.petitparser.parser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests {@link MatchesIterator}.
 */
public class MatchesIteratorTest {

  @Test
  public void testPropertiesAndIteration() {
    Parser parser = of('x');
    MatchesIterator<Character> iterator =
        new MatchesIterator<>(parser, "x_x", 0, false);

    assertEquals(parser, iterator.getParser());
    assertEquals("x_x", iterator.getInput());
    assertEquals(0, iterator.getStart());
    assertEquals(0, iterator.getCurrent());
    assertFalse(iterator.isOverlapping());
    assertTrue(iterator.toString().contains("MatchesIterator"));

    List<Character> list = new ArrayList<>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    assertEquals(Arrays.asList('x', 'x'), list);
    assertFalse(iterator.hasNext());
    assertTrue(iterator.getCurrent() > 0);
  }

  @Test(expected = NoSuchElementException.class)
  public void testNextBeyondExhaustionThrows() {
    MatchesIterator<Character> iterator =
        new MatchesIterator<>(of('x'), "x", 0, false);
    assertTrue(iterator.hasNext());
    assertEquals(Character.valueOf('x'), iterator.next());
    assertFalse(iterator.hasNext());
    iterator.next();
  }

  @Test
  public void testOverlappingIteration() {
    Parser parser = digit().seq(digit()).flatten();
    MatchesIterator<String> iterator =
        new MatchesIterator<>(parser, "123", 0, true);

    List<String> list = new ArrayList<>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    assertEquals(Arrays.asList("12", "23"), list);
  }

  @Test
  public void testNonOverlappingIteration() {
    Parser parser = digit().seq(digit()).flatten();
    MatchesIterator<String> iterator =
        new MatchesIterator<>(parser, "1234", 0, false);

    List<String> list = new ArrayList<>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    assertEquals(Arrays.asList("12", "34"), list);
  }

  @Test
  public void testZeroLengthMatchProgresses() {
    Parser parser = digit().star().flatten();
    MatchesIterator<String> iterator =
        new MatchesIterator<>(parser, "a12b", 0, false);

    List<String> list = new ArrayList<>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    assertEquals(Arrays.asList("", "12", "", ""), list);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testNegativeStartThrows() {
    new MatchesIterator<>(of('a'), "abc", -1, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullParserThrows() {
    new MatchesIterator<>(null, "abc", 0, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullInputThrows() {
    new MatchesIterator<>(of('a'), null, 0, false);
  }
}
