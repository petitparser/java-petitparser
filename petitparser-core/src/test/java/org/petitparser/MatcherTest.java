package org.petitparser;

import org.junit.Test;
import org.petitparser.context.Result;
import org.petitparser.parser.MatchesIterable;
import org.petitparser.parser.MatchesIterator;
import org.petitparser.parser.MatchesSpliterator;
import org.petitparser.parser.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.EpsilonParser.epsilon;

/**
 * Tests for matcher extensions on {@link Parser} and lazy matching structures.
 */
public class MatcherTest {

  @Test
  public void testParse() {
    Parser parser = of('a');
    assertTrue(parser.parse("a").isSuccess());
    assertFalse(parser.parse("b").isSuccess());
  }

  @Test
  public void testParseWithStart() {
    Parser parser = of('b');
    assertFalse(parser.parse("abc", 0).isSuccess());
    assertTrue(parser.parse("abc", 1).isSuccess());
    assertFalse(parser.parse("abc", 2).isSuccess());
    assertFalse(parser.parse("abc", 3).isSuccess());
    assertFalse(parser.parse("abc", 4).isSuccess());
    assertFalse(parser.parse("abc", -1).isSuccess());
  }

  @Test
  public void testAccept() {
    Parser parser = of('a');
    assertTrue(parser.accept("a"));
    assertFalse(parser.accept("b"));
  }

  @Test
  public void testAcceptWithStart() {
    Parser parser = of('b');
    assertFalse(parser.accept("abc", 0));
    assertTrue(parser.accept("abc", 1));
    assertFalse(parser.accept("abc", 2));
    assertFalse(parser.accept("abc", 3));
    assertFalse(parser.accept("abc", 4));
    assertFalse(parser.accept("abc", -1));
    assertFalse(parser.accept(null, 0));
  }

  @Test
  public void testAcceptEpsilonAtEnd() {
    Parser parser = epsilon();
    assertTrue(parser.accept("abc", 3));
    assertFalse(parser.accept("abc", 4));
  }

  @Test
  public void testAllMatchesOverlapping() {
    String input = "a123b456";
    Parser parser = digit().seq(digit()).flatten();

    List<String> expected = Arrays.asList("12", "23", "45", "56");
    List<String> actual = parser.matches(input);
    assertEquals(expected, actual);

    List<String> actualFromStart0 = new ArrayList<>();
    parser.<String>matches(input, 0).forEach(actualFromStart0::add);
    assertEquals(expected, actualFromStart0);

    List<String> actualFromStart3 = new ArrayList<>();
    parser.<String>matches(input, 3).forEach(actualFromStart3::add);
    assertEquals(Arrays.asList("45", "56"), actualFromStart3);
  }

  @Test
  public void testAllMatchesNonOverlapping() {
    String input = "a123b456";
    Parser parser = digit().seq(digit()).flatten();

    List<String> expected = Arrays.asList("12", "45");
    List<String> actual = parser.matchesSkipping(input);
    assertEquals(expected, actual);

    List<String> actualFromStart0 = new ArrayList<>();
    parser.<String>matchesSkipping(input, 0).forEach(actualFromStart0::add);
    assertEquals(expected, actualFromStart0);

    List<String> actualFromStart3 = new ArrayList<>();
    parser.<String>matchesSkipping(input, 3).forEach(actualFromStart3::add);
    assertEquals(Collections.singletonList("45"), actualFromStart3);
  }

  @Test
  public void testMatchesEmptyMatch() {
    String input = "a123b45";
    Parser parser = digit().star().flatten();

    List<String> actualNonOverlapping = new ArrayList<>();
    parser.<String>matches(input, 0, false).forEach(actualNonOverlapping::add);
    assertEquals(Arrays.asList("", "123", "", "45", ""), actualNonOverlapping);
  }

  @Test
  public void testMatchesBeyondLength() {
    Parser parser = digit();
    List<String> actual = new ArrayList<>();
    parser.<String>matches("123", 10).forEach(actual::add);
    assertTrue(actual.isEmpty());

    List<String> actualSkipping = new ArrayList<>();
    parser.<String>matchesSkipping("123", 10).forEach(actualSkipping::add);
    assertTrue(actualSkipping.isEmpty());
  }

  @Test
  public void testMatchesStreamLazy() {
    AtomicInteger invocationCount = new AtomicInteger(0);
    Parser parser = digit().seq(digit()).flatten()
        .map(val -> {
          invocationCount.incrementAndGet();
          return val;
        });

    Stream<String> stream = parser.matchesAsStream("a123b456");
    // Stream created, no elements evaluated yet
    assertEquals(0, invocationCount.get());

    String first = stream.findFirst().orElse(null);
    assertEquals("12", first);
    // Only the first match should have been evaluated (1 instead of 4)
    assertEquals(1, invocationCount.get());
  }

  @Test
  public void testMatchesAsStreamOverloads() {
    String input = "a123b456";
    Parser parser = digit().seq(digit()).flatten();

    List<String> overlapping0 = parser.<String>matchesAsStream(input)
        .collect(Collectors.toList());
    assertEquals(Arrays.asList("12", "23", "45", "56"), overlapping0);

    List<String> overlapping3 = parser.<String>matchesAsStream(input, 3)
        .collect(Collectors.toList());
    assertEquals(Arrays.asList("45", "56"), overlapping3);

    List<String> nonOverlapping0 = parser.<String>matchesSkippingAsStream(input)
        .collect(Collectors.toList());
    assertEquals(Arrays.asList("12", "45"), nonOverlapping0);

    List<String> nonOverlapping3 = parser.<String>matchesSkippingAsStream(input, 3)
        .collect(Collectors.toList());
    assertEquals(Collections.singletonList("45"), nonOverlapping3);
  }

  @Test
  public void testMatchesSpliteratorDirectly() {
    Parser parser = digit().seq(digit()).flatten();
    String input = "a123b45";
    MatchesSpliterator<String> spliterator =
        new MatchesSpliterator<>(parser, input, 1, false);

    assertEquals(parser, spliterator.getParser());
    assertEquals(input, spliterator.getInput());
    assertEquals(1, spliterator.getStart());
    assertFalse(spliterator.isOverlapping());
    assertNull(spliterator.trySplit());
    assertEquals(Long.MAX_VALUE, spliterator.estimateSize());
    assertEquals(Spliterator.ORDERED, spliterator.characteristics());
    assertTrue(spliterator.toString().contains("MatchesSpliterator"));

    List<String> collected = new ArrayList<>();
    spliterator.forEachRemaining(collected::add);
    assertEquals(Arrays.asList("12", "45"), collected);
  }

  @Test
  public void testMatchesIteratorDirectly() {
    Parser parser = of('x');
    String input = "x_x_x";
    MatchesIterator<Character> iterator =
        new MatchesIterator<>(parser, input, 0, false);

    assertEquals(parser, iterator.getParser());
    assertEquals(input, iterator.getInput());
    assertEquals(0, iterator.getStart());
    assertFalse(iterator.isOverlapping());
    assertTrue(iterator.toString().contains("MatchesIterator"));

    assertTrue(iterator.hasNext());
    assertEquals(Character.valueOf('x'), iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(Character.valueOf('x'), iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(Character.valueOf('x'), iterator.next());
    assertFalse(iterator.hasNext());

    try {
      iterator.next();
      org.junit.Assert.fail("Expected NoSuchElementException");
    } catch (NoSuchElementException expected) {
      // Expected
    }
  }

  @Test
  public void testMatchesIterableDirectly() {
    Parser parser = of('x');
    String input = "x_x";
    MatchesIterable<Character> iterable =
        new MatchesIterable<>(parser, input, 0, false);

    assertEquals(parser, iterable.getParser());
    assertEquals(input, iterable.getInput());
    assertEquals(0, iterable.getStart());
    assertFalse(iterable.isOverlapping());
    assertTrue(iterable.toString().contains("MatchesIterable"));

    // Iterating twice produces independent iterators
    List<Character> firstList = new ArrayList<>();
    iterable.forEach(firstList::add);
    assertEquals(Arrays.asList('x', 'x'), firstList);

    List<Character> secondList = new ArrayList<>();
    iterable.forEach(secondList::add);
    assertEquals(Arrays.asList('x', 'x'), secondList);

    List<Character> streamList = iterable.stream().collect(Collectors.toList());
    assertEquals(Arrays.asList('x', 'x'), streamList);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testNegativeStartThrowsInSpliterator() {
    new MatchesSpliterator<>(of('a'), "abc", -1, false);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testNegativeStartThrowsInIterator() {
    new MatchesIterator<>(of('a'), "abc", -1, false);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testNegativeStartThrowsInIterable() {
    new MatchesIterable<>(of('a'), "abc", -1, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullParserThrowsInSpliterator() {
    new MatchesSpliterator<>(null, "abc", 0, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullInputThrowsInSpliterator() {
    new MatchesSpliterator<>(of('a'), null, 0, false);
  }

  @Test(expected = NullPointerException.class)
  public void testNullActionThrowsInSpliteratorAdvance() {
    MatchesSpliterator<Object> spliterator =
        new MatchesSpliterator<>(of('a'), "abc", 0, false);
    spliterator.tryAdvance(null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullActionThrowsInSpliteratorForEachRemaining() {
    MatchesSpliterator<Object> spliterator =
        new MatchesSpliterator<>(of('a'), "abc", 0, false);
    spliterator.forEachRemaining(null);
  }
}
