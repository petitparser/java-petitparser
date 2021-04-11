package org.petitparser.utils;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.petitparser.utils.Functions.constant;
import static org.petitparser.utils.Functions.firstOfList;
import static org.petitparser.utils.Functions.lastOfList;
import static org.petitparser.utils.Functions.nthOfList;
import static org.petitparser.utils.Functions.permutationOfList;
import static org.petitparser.utils.Functions.withoutSeparators;

import org.junit.Test;

import java.util.List;

/**
 * Tests {@link Functions}.
 */
public class FunctionsTest {

  static final List<Object> EMPTY = asList();
  static final List<Object> ONE = asList('a');
  static final List<Object> TWO = asList('a', 'b');
  static final List<Object> THREE = asList('a', 'b', 'c');

  @Test
  public void testFirstOfList() {
    assertEquals('a', firstOfList().apply(ONE));
    assertEquals('a', firstOfList().apply(TWO));
    assertEquals('a', firstOfList().apply(THREE));
  }

  @Test
  public void testLastOfList() {
    assertEquals('a', lastOfList().apply(ONE));
    assertEquals('b', lastOfList().apply(TWO));
    assertEquals('c', lastOfList().apply(THREE));
  }

  @Test
  public void testNthOfListOfList() {
    assertEquals('a', nthOfList(0).apply(ONE));
    assertEquals('a', nthOfList(0).apply(TWO));
    assertEquals('a', nthOfList(0).apply(THREE));

    assertEquals('b', nthOfList(1).apply(TWO));
    assertEquals('b', nthOfList(1).apply(THREE));

    assertEquals('c', nthOfList(2).apply(THREE));

    assertEquals('a', nthOfList(-1).apply(ONE));
    assertEquals('b', nthOfList(-1).apply(TWO));
    assertEquals('c', nthOfList(-1).apply(THREE));

    assertEquals('a', nthOfList(-2).apply(TWO));
    assertEquals('b', nthOfList(-2).apply(THREE));

    assertEquals('a', nthOfList(-3).apply(THREE));
  }

  @Test
  public void testPermutationOfList() {
    assertEquals(EMPTY, permutationOfList().apply(EMPTY));
    assertEquals(EMPTY, permutationOfList().apply(THREE));
    assertEquals(asList('c', 'a'), permutationOfList(-1, 0).apply(THREE));
    assertEquals(asList('a', 'a'), permutationOfList(-3, 0).apply(THREE));
  }

  @Test
  public void testWithoutSeparators() {
    assertEquals(EMPTY, withoutSeparators().apply(EMPTY));
    assertEquals(ONE, withoutSeparators().apply(ONE));
    assertEquals(ONE, withoutSeparators().apply(TWO));
    assertEquals(asList('a', 'c'), withoutSeparators().apply(THREE));
  }

  @Test
  public void testConstant() {
    assertEquals((Object) 'a', constant('a').apply('b'));
    assertEquals((Object) 'b', constant('b').apply('c'));
  }
}
