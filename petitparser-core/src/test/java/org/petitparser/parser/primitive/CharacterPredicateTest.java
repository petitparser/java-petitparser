package org.petitparser.parser.primitive;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link CharacterPredicate}.
 */
public class CharacterPredicateTest {

  @Test
  public void testAny() {
    CharacterPredicate predicate = CharacterPredicate.any();
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
  }

  @Test
  public void testAnyOf() {
    CharacterPredicate predicate = CharacterPredicate.anyOf("uncopyrightable");
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('g'));
    assertTrue(predicate.test('h'));
    assertTrue(predicate.test('i'));
    assertTrue(predicate.test('o'));
    assertTrue(predicate.test('p'));
    assertTrue(predicate.test('r'));
    assertTrue(predicate.test('t'));
    assertTrue(predicate.test('y'));
    assertFalse(predicate.test('x'));
  }

  @Test
  public void testAnyOfEmpty() {
    CharacterPredicate predicate = CharacterPredicate.anyOf("");
    assertFalse(predicate.test('a'));
    assertFalse(predicate.test('b'));
  }

  @Test
  public void testNone() {
    CharacterPredicate predicate = CharacterPredicate.none();
    assertFalse(predicate.test('a'));
    assertFalse(predicate.test('b'));
  }

  @Test
  public void testNoneOf() {
    CharacterPredicate predicate = CharacterPredicate.noneOf("uncopyrightable");
    assertTrue(predicate.test('x'));
    assertFalse(predicate.test('c'));
    assertFalse(predicate.test('g'));
    assertFalse(predicate.test('h'));
    assertFalse(predicate.test('i'));
    assertFalse(predicate.test('o'));
    assertFalse(predicate.test('p'));
    assertFalse(predicate.test('r'));
    assertFalse(predicate.test('t'));
    assertFalse(predicate.test('y'));
  }

  @Test
  public void testNoneOfEmpty() {
    CharacterPredicate predicate = CharacterPredicate.noneOf("");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
  }

  @Test
  public void testOf() {
    CharacterPredicate predicate = CharacterPredicate.of('a');
    assertTrue(predicate.test('a'));
    assertFalse(predicate.test('b'));
  }

  @Test
  public void testNot() {
    CharacterPredicate source = CharacterPredicate.of('a');
    CharacterPredicate predicate = source.not();
    assertFalse(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertSame(source, predicate.not());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRangesInvalidSize() {
    CharacterPredicate.ranges(new char[]{}, new char[]{'a'});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRangesInvalidOrder() {
    CharacterPredicate.ranges(new char[]{'b'}, new char[]{'a'});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRangesInvalidSequence() {
    CharacterPredicate.ranges(new char[]{'a', 'c'}, new char[]{'c', 'f'});
  }

  @Test
  public void testPatternWithSingle() {
    CharacterPredicate predicate = CharacterPredicate.pattern("abc");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertFalse(predicate.test('d'));
  }

  @Test
  public void testPatternWithRange() {
    CharacterPredicate predicate = CharacterPredicate.pattern("a-c");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertFalse(predicate.test('d'));
  }

  @Test
  public void testPatternWithOverlappingRange() {
    CharacterPredicate predicate = CharacterPredicate.pattern("b-da-c");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertFalse(predicate.test('e'));
  }

  @Test
  public void testPatternWithAdjacentRange() {
    CharacterPredicate predicate = CharacterPredicate.pattern("c-ea-c");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertFalse(predicate.test('f'));
  }

  @Test
  public void testPatternWithPrefixRange() {
    CharacterPredicate predicate = CharacterPredicate.pattern("a-ea-c");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertFalse(predicate.test('f'));
  }

  @Test
  public void testPatternWithPostfixRange() {
    CharacterPredicate predicate = CharacterPredicate.pattern("a-ec-e");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertFalse(predicate.test('f'));
  }

  @Test
  public void testPatternWithRepeatedRange() {
    CharacterPredicate predicate = CharacterPredicate.pattern("a-ea-e");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertFalse(predicate.test('f'));
  }

  @Test
  public void testPatternWithComposed() {
    CharacterPredicate predicate = CharacterPredicate.pattern("ac-df-");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('f'));
    assertTrue(predicate.test('-'));
    assertFalse(predicate.test('b'));
    assertFalse(predicate.test('e'));
    assertFalse(predicate.test('g'));
  }

  @Test
  public void testPatternWithNegatedSingle() {
    CharacterPredicate predicate = CharacterPredicate.pattern("^a");
    assertTrue(predicate.test('b'));
    assertFalse(predicate.test('a'));
  }

  @Test
  public void testPatternWithNegatedRange() {
    CharacterPredicate predicate = CharacterPredicate.pattern("^a-c");
    assertTrue(predicate.test('d'));
    assertFalse(predicate.test('a'));
    assertFalse(predicate.test('b'));
    assertFalse(predicate.test('c'));
  }

  @Test
  public void testRange() {
    CharacterPredicate predicate = CharacterPredicate.range('e', 'o');
    assertFalse(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertTrue(predicate.test('i'));
    assertTrue(predicate.test('o'));
    assertFalse(predicate.test('p'));
  }
}
