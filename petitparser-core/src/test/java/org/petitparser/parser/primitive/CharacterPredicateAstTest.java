package org.petitparser.parser.primitive;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests AST character predicates and their equality semantics.
 */
public class CharacterPredicateAstTest {

  @Test
  public void testSingleCharPredicate() {
    SingleCharPredicate predicate = new SingleCharPredicate('a');
    assertEquals('a', predicate.getValue());
    assertTrue(predicate.test('a'));
    assertFalse(predicate.test('b'));
    assertTrue(predicate.test((int) 'a'));
    assertFalse(predicate.test((int) 'b'));

    SingleCharPredicate same = new SingleCharPredicate('a');
    SingleCharPredicate different = new SingleCharPredicate('b');

    assertTrue(predicate.isEqualTo(same));
    assertFalse(predicate.isEqualTo(different));
    assertEquals(predicate, same);
    assertNotEquals(predicate, different);
    assertEquals(predicate.hashCode(), same.hashCode());
    assertTrue(predicate.toString().contains("97"));
  }

  @Test
  public void testRangeCharPredicate() {
    RangeCharPredicate predicate = new RangeCharPredicate('a', 'z');
    assertEquals('a', predicate.getStart());
    assertEquals('z', predicate.getStop());
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('m'));
    assertTrue(predicate.test('z'));
    assertFalse(predicate.test('`'));
    assertFalse(predicate.test('{'));
    assertTrue(predicate.test((int) 'm'));

    RangeCharPredicate same = new RangeCharPredicate('a', 'z');
    RangeCharPredicate different = new RangeCharPredicate('a', 'y');

    assertTrue(predicate.isEqualTo(same));
    assertFalse(predicate.isEqualTo(different));
    assertEquals(predicate, same);
    assertNotEquals(predicate, different);
    assertEquals(predicate.hashCode(), same.hashCode());
    assertTrue(predicate.toString().contains("97..122"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRangeCharPredicateInvalid() {
    new RangeCharPredicate('z', 'a');
  }

  @Test
  public void testRangesCharPredicate() {
    RangesCharPredicate predicate = new RangesCharPredicate(
        new char[]{'0', 'a'}, new char[]{'9', 'z'});
    assertArrayEquals(new int[]{'0', 'a'}, predicate.getStarts());
    assertArrayEquals(new int[]{'9', 'z'}, predicate.getStops());
    assertTrue(predicate.test('5'));
    assertTrue(predicate.test('c'));
    assertFalse(predicate.test('@'));
    assertTrue(predicate.test((int) '5'));

    RangesCharPredicate same = RangesCharPredicate.fromRanges(Arrays.asList(
        new RangeCharPredicate('0', '9'),
        new RangeCharPredicate('a', 'z')));
    RangesCharPredicate different = new RangesCharPredicate(
        new char[]{'0', 'a'}, new char[]{'8', 'z'});

    assertTrue(predicate.isEqualTo(same));
    assertFalse(predicate.isEqualTo(different));
    assertEquals(predicate, same);
    assertNotEquals(predicate, different);
    assertEquals(predicate.hashCode(), same.hashCode());
    assertTrue(predicate.toString().contains("2 ranges"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRangesCharPredicateInvalidSize() {
    new RangesCharPredicate(new int[]{1}, new int[]{1, 2});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRangesCharPredicateInvalidOrder() {
    new RangesCharPredicate(new int[]{5}, new int[]{2});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRangesCharPredicateInvalidSequence() {
    new RangesCharPredicate(new int[]{1, 3}, new int[]{4, 5});
  }

  @Test
  public void testLookupCharPredicate() {
    List<RangeCharPredicate> ranges = Arrays.asList(
        new RangeCharPredicate('a', 'c'),
        new RangeCharPredicate('x', 'z'));
    LookupCharPredicate predicate = LookupCharPredicate.fromRanges(ranges);

    assertEquals('a', predicate.getStart());
    assertEquals('z', predicate.getStop());
    assertTrue(predicate.getBits().length > 0);

    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertFalse(predicate.test('d'));
    assertTrue(predicate.test('x'));
    assertTrue(predicate.test('y'));
    assertTrue(predicate.test('z'));
    assertFalse(predicate.test('w'));
    assertFalse(predicate.test((char) ('a' - 1)));
    assertFalse(predicate.test((char) ('z' + 1)));

    LookupCharPredicate same = LookupCharPredicate.fromRanges(ranges);
    LookupCharPredicate different = LookupCharPredicate.fromRanges(
        Arrays.asList(new RangeCharPredicate('a', 'c')));

    assertTrue(predicate.isEqualTo(same));
    assertFalse(predicate.isEqualTo(different));
    assertEquals(predicate, same);
    assertNotEquals(predicate, different);
    assertEquals(predicate.hashCode(), same.hashCode());
    assertTrue(predicate.toString().contains("97..122"));

    boolean[] table = new boolean[256];
    table['a'] = true;
    table['b'] = true;
    LookupCharPredicate fromTable = new LookupCharPredicate(table);
    assertTrue(fromTable.test('a'));
    assertTrue(fromTable.test('b'));
    assertFalse(fromTable.test('c'));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLookupCharPredicateEmpty() {
    LookupCharPredicate.fromRanges(List.of());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLookupCharPredicateInvalidRange() {
    new LookupCharPredicate(10, 5, new int[]{0});
  }

  @Test
  public void testNotCharPredicate() {
    CharacterPredicate inner = new SingleCharPredicate('a');
    CharacterPredicate not = inner.not();

    assertTrue(not instanceof NotCharPredicate);
    assertSame(inner, ((NotCharPredicate) not).getPredicate());
    assertFalse(not.test('a'));
    assertTrue(not.test('b'));
    assertTrue(not.test((int) 'b'));

    assertSame(inner, not.not());

    CharacterPredicate same = new SingleCharPredicate('a').not();
    CharacterPredicate different = new SingleCharPredicate('b').not();

    assertTrue(not.isEqualTo(same));
    assertFalse(not.isEqualTo(different));
    assertEquals(not, same);
    assertNotEquals(not, different);
    assertEquals(not.hashCode(), same.hashCode());
    assertTrue(not.toString().contains("NotCharPredicate"));
  }

  @Test
  public void testConstantCharPredicate() {
    ConstantCharPredicate any = ConstantCharPredicate.any();
    ConstantCharPredicate none = ConstantCharPredicate.none();

    assertTrue(any.getResult());
    assertFalse(none.getResult());

    assertTrue(any.test('x'));
    assertTrue(any.test(12345));
    assertFalse(none.test('x'));
    assertFalse(none.test(12345));

    assertSame(none, any.not());
    assertSame(any, none.not());

    assertTrue(any.isEqualTo(ConstantCharPredicate.ANY));
    assertFalse(any.isEqualTo(none));
    assertEquals(any, ConstantCharPredicate.ANY);
    assertNotEquals(any, none);
    assertEquals(any.hashCode(), ConstantCharPredicate.ANY.hashCode());
    assertTrue(any.toString().contains("true"));
  }

  @Test
  public void testSingletons() {
    // Digit
    assertTrue(DigitCharPredicate.INSTANCE.test('5'));
    assertFalse(DigitCharPredicate.INSTANCE.test('a'));
    assertTrue(DigitCharPredicate.INSTANCE.test((int) '5'));
    assertTrue(DigitCharPredicate.INSTANCE.isEqualTo(new DigitCharPredicate()));
    assertEquals(DigitCharPredicate.INSTANCE, new DigitCharPredicate());
    assertEquals("DigitCharPredicate", DigitCharPredicate.INSTANCE.toString());

    // Letter
    assertTrue(LetterCharPredicate.INSTANCE.test('a'));
    assertTrue(LetterCharPredicate.INSTANCE.test('Z'));
    assertFalse(LetterCharPredicate.INSTANCE.test('5'));
    assertTrue(LetterCharPredicate.INSTANCE.test((int) 'a'));
    assertTrue(LetterCharPredicate.INSTANCE.isEqualTo(new LetterCharPredicate()));
    assertEquals(LetterCharPredicate.INSTANCE, new LetterCharPredicate());
    assertEquals("LetterCharPredicate", LetterCharPredicate.INSTANCE.toString());

    // Lowercase
    assertTrue(LowercaseCharPredicate.INSTANCE.test('a'));
    assertFalse(LowercaseCharPredicate.INSTANCE.test('A'));
    assertTrue(LowercaseCharPredicate.INSTANCE.test((int) 'a'));
    assertTrue(LowercaseCharPredicate.INSTANCE.isEqualTo(new LowercaseCharPredicate()));
    assertEquals(LowercaseCharPredicate.INSTANCE, new LowercaseCharPredicate());
    assertEquals("LowercaseCharPredicate", LowercaseCharPredicate.INSTANCE.toString());

    // Uppercase
    assertTrue(UppercaseCharPredicate.INSTANCE.test('A'));
    assertFalse(UppercaseCharPredicate.INSTANCE.test('a'));
    assertTrue(UppercaseCharPredicate.INSTANCE.test((int) 'A'));
    assertTrue(UppercaseCharPredicate.INSTANCE.isEqualTo(new UppercaseCharPredicate()));
    assertEquals(UppercaseCharPredicate.INSTANCE, new UppercaseCharPredicate());
    assertEquals("UppercaseCharPredicate", UppercaseCharPredicate.INSTANCE.toString());

    // Whitespace
    assertTrue(WhitespaceCharPredicate.INSTANCE.test(' '));
    assertTrue(WhitespaceCharPredicate.INSTANCE.test('\t'));
    assertFalse(WhitespaceCharPredicate.INSTANCE.test('a'));
    assertTrue(WhitespaceCharPredicate.INSTANCE.test((int) ' '));
    assertTrue(WhitespaceCharPredicate.INSTANCE.isEqualTo(new WhitespaceCharPredicate()));
    assertEquals(WhitespaceCharPredicate.INSTANCE, new WhitespaceCharPredicate());
    assertEquals("WhitespaceCharPredicate", WhitespaceCharPredicate.INSTANCE.toString());

    // Word
    assertTrue(WordCharPredicate.INSTANCE.test('a'));
    assertTrue(WordCharPredicate.INSTANCE.test('9'));
    assertFalse(WordCharPredicate.INSTANCE.test(' '));
    assertTrue(WordCharPredicate.INSTANCE.test((int) 'a'));
    assertTrue(WordCharPredicate.INSTANCE.isEqualTo(new WordCharPredicate()));
    assertEquals(WordCharPredicate.INSTANCE, new WordCharPredicate());
    assertEquals("WordCharPredicate", WordCharPredicate.INSTANCE.toString());
  }

  @Test
  public void testCharacterParserStructuralEquality() {
    assertTrue(CharacterParser.digit().isEqualTo(CharacterParser.digit()));
    assertTrue(CharacterParser.letter().isEqualTo(CharacterParser.letter()));
    assertTrue(CharacterParser.lowerCase().isEqualTo(CharacterParser.lowerCase()));
    assertTrue(CharacterParser.upperCase().isEqualTo(CharacterParser.upperCase()));
    assertTrue(CharacterParser.whitespace().isEqualTo(CharacterParser.whitespace()));
    assertTrue(CharacterParser.word().isEqualTo(CharacterParser.word()));
    assertTrue(CharacterParser.any().isEqualTo(CharacterParser.any()));
    assertTrue(CharacterParser.none().isEqualTo(CharacterParser.none()));
    assertTrue(CharacterParser.of('a').isEqualTo(CharacterParser.of('a')));
    assertTrue(CharacterParser.range('a', 'z').isEqualTo(CharacterParser.range('a', 'z')));

    assertFalse(CharacterParser.digit().isEqualTo(CharacterParser.letter()));
    assertFalse(CharacterParser.of('a').isEqualTo(CharacterParser.of('b')));
    assertFalse(CharacterParser.range('a', 'z').isEqualTo(CharacterParser.range('a', 'y')));
  }
}
