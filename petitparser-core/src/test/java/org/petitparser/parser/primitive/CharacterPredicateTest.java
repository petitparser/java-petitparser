package org.petitparser.parser.primitive;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
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

  @Test
  public void testAnyOfOverloads() {
    CharacterPredicate p1 = CharacterPredicate.anyOf("abc", true);
    assertTrue(p1.test('a'));
    assertFalse(p1.test('d'));

    CharacterPredicate p2 = CharacterPredicate.anyOf("aB", true, false);
    assertTrue(p2.test('a'));
    assertTrue(p2.test('A'));
    assertTrue(p2.test('b'));
    assertTrue(p2.test('B'));
    assertFalse(p2.test('c'));
  }

  @Test
  public void testNoneOfOverloads() {
    CharacterPredicate p1 = CharacterPredicate.noneOf("abc", true);
    assertFalse(p1.test('a'));
    assertTrue(p1.test('d'));

    CharacterPredicate p2 = CharacterPredicate.noneOf("aB", true, false);
    assertFalse(p2.test('a'));
    assertFalse(p2.test('A'));
    assertFalse(p2.test('b'));
    assertFalse(p2.test('B'));
    assertTrue(p2.test('c'));
  }

  @Test
  public void testRangeOverloads() {
    CharacterPredicate p1 = CharacterPredicate.range(0x61, 0x63);
    assertTrue(p1.test('a'));
    assertTrue(p1.test('b'));
    assertTrue(p1.test('c'));
    assertFalse(p1.test('d'));
  }

  @Test
  public void testRangesOverloads() {
    CharacterPredicate p1 = CharacterPredicate.ranges(new char[]{'a', 'e'}, new char[]{'c', 'g'});
    assertTrue(p1.test('b'));
    assertTrue(p1.test('f'));
    assertFalse(p1.test('d'));

    CharacterPredicate p2 = CharacterPredicate.ranges(new int[]{0x61, 0x65}, new int[]{0x63, 0x67});
    assertTrue(p2.test('b'));
    assertTrue(p2.test('f'));
    assertFalse(p2.test('d'));
  }

  @Test
  public void testPatternOverloads() {
    CharacterPredicate p1 = CharacterPredicate.pattern("a-c", true);
    assertTrue(p1.test('a'));
    assertTrue(p1.test('b'));
    assertFalse(p1.test('d'));

    CharacterPredicate p2 = CharacterPredicate.pattern("a-c", true, false);
    assertTrue(p2.test('a'));
    assertTrue(p2.test('A'));
    assertTrue(p2.test('b'));
    assertTrue(p2.test('B'));
    assertFalse(p2.test('d'));
  }

  @Test
  public void testOfOverloads() {
    CharacterPredicate p1 = CharacterPredicate.of('a');
    assertTrue(p1.test('a'));
    assertFalse(p1.test('b'));

    CharacterPredicate p2 = CharacterPredicate.of(0x1F680);
    assertTrue(p2.test(0x1F680));
    assertFalse(p2.test('a'));
  }

  @Test
  public void testOptimizedStringAndRanges() {
    CharacterPredicate p1 = CharacterPredicate.optimizedString("abc", true);
    assertTrue(p1.test('a'));
    assertTrue(p1.test('A'));
    assertFalse(p1.test('d'));

    CharacterPredicate p2 = CharacterPredicate.optimizedRanges(
        List.of(new RangeCharPredicate('a', 'c'), new RangeCharPredicate('e', 'g')));
    assertTrue(p2.test('b'));
    assertTrue(p2.test('f'));
    assertFalse(p2.test('d'));
  }

  @Test
  @SuppressWarnings("deprecation")
  public void testDeprecatedNotCharacterPredicate() {
    CharacterPredicate inner = CharacterPredicate.of('a');
    CharacterPredicate.NotCharacterPredicate notPred =
        new CharacterPredicate.NotCharacterPredicate(inner);
    assertFalse(notPred.test('a'));
    assertTrue(notPred.test('b'));
    assertEquals(inner, notPred.not());
    assertTrue(notPred.isEqualTo(inner.not()));
  }

  @Test
  public void testSingletonPredicatesHashCode() {
    assertTrue(CharacterPredicate.any().hashCode() != 0);
    assertTrue(CharacterPredicate.none().hashCode() != 0);
    assertTrue(DigitCharPredicate.INSTANCE.hashCode() != 0);
    assertTrue(LetterCharPredicate.INSTANCE.hashCode() != 0);
    assertTrue(LowercaseCharPredicate.INSTANCE.hashCode() != 0);
    assertTrue(UppercaseCharPredicate.INSTANCE.hashCode() != 0);
    assertTrue(WhitespaceCharPredicate.INSTANCE.hashCode() != 0);
    assertTrue(WordCharPredicate.INSTANCE.hashCode() != 0);
  }

  @Test
  public void testDefaultInterfaceMethods() {
    CharacterPredicate custom = (char c) -> c == 'z';
    assertTrue(custom.test((int) 'z'));
    assertFalse(custom.test((int) 'a'));
    assertFalse(custom.test(-1));
    assertFalse(custom.test(0x10000));
    assertTrue(custom.isEqualTo(custom));
  }

  @Test
  public void testRangeCharPredicateOptimizedRangesDirect() {
    CharacterPredicate p1 = RangeCharPredicate.optimizedRanges(
        List.of(new RangeCharPredicate('0', '9')));
    assertTrue(p1.test('5'));
    assertFalse(p1.test('a'));

    CharacterPredicate p2 = RangeCharPredicate.optimizedRanges(
        List.of(new RangeCharPredicate('0', '9')), true);
    assertTrue(p2.test('5'));
    assertFalse(p2.test('a'));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLookupCharPredicateEmpty() {
    new LookupCharPredicate(new boolean[0]);
  }
}
