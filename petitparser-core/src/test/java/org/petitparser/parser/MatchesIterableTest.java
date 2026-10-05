package org.petitparser.parser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests {@link MatchesIterable}.
 */
public class MatchesIterableTest {

  @Test
  public void testPropertiesAndMultipleIterations() {
    Parser parser = of('x');
    MatchesIterable<Character> iterable =
        new MatchesIterable<>(parser, "x_x", 0, false);

    assertEquals(parser, iterable.getParser());
    assertEquals("x_x", iterable.getInput());
    assertEquals(0, iterable.getStart());
    assertFalse(iterable.isOverlapping());
    assertTrue(iterable.toString().contains("MatchesIterable"));

    List<Character> first = new ArrayList<>();
    iterable.forEach(first::add);
    assertEquals(Arrays.asList('x', 'x'), first);

    List<Character> second = new ArrayList<>();
    iterable.forEach(second::add);
    assertEquals(Arrays.asList('x', 'x'), second);

    assertNotNull(iterable.iterator());
    assertNotNull(iterable.spliterator());
    assertEquals(Arrays.asList('x', 'x'), iterable.stream().collect(Collectors.toList()));
  }

  @Test
  public void testOverlappingIterable() {
    Parser parser = digit().seq(digit()).flatten();
    MatchesIterable<String> iterable =
        new MatchesIterable<>(parser, "123", 0, true);

    assertTrue(iterable.isOverlapping());
    List<String> list = new ArrayList<>();
    iterable.forEach(list::add);
    assertEquals(Arrays.asList("12", "23"), list);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testNegativeStartThrows() {
    new MatchesIterable<>(of('a'), "abc", -1, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullParserThrows() {
    new MatchesIterable<>(null, "abc", 0, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullInputThrows() {
    new MatchesIterable<>(of('a'), null, 0, false);
  }
}
