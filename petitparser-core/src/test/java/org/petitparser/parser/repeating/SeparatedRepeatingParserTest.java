package org.petitparser.parser.repeating;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.SequentialParser;
import org.petitparser.parser.primitive.CharacterParser;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests for {@link SeparatedRepeatingParser}.
 */
public class SeparatedRepeatingParserTest {

  private final Parser element = CharacterParser.of('a');
  private final Parser separator = CharacterParser.of(',');

  @Test
  public void testStarSeparated() {
    Parser parser = element.starSeparated(separator);
    assertTrue(parser instanceof SeparatedRepeatingParser);
    assertTrue(parser instanceof SequentialParser);

    assertSuccess(parser, "", new SeparatedList<Character, Character>(), 0);
    assertSuccess(parser, "b", new SeparatedList<Character, Character>(), 0);
    assertSuccess(parser, "a", new SeparatedList<>(Collections.singletonList('a')), 1);
    assertSuccess(parser, "a,a", new SeparatedList<>(
        Arrays.asList('a', 'a'), Collections.singletonList(',')), 3);
    assertSuccess(parser, "a,a,a", new SeparatedList<>(
        Arrays.asList('a', 'a', 'a'), Arrays.asList(',', ',')), 5);
    assertSuccess(parser, "a,b", new SeparatedList<>(Collections.singletonList('a')), 1);
    assertSuccess(parser, "a,", new SeparatedList<>(Collections.singletonList('a')), 1);
  }

  @Test
  public void testPlusSeparated() {
    Parser parser = element.plusSeparated(separator);
    assertTrue(parser instanceof SeparatedRepeatingParser);

    assertFailure(parser, "", 0, "'a' expected");
    assertFailure(parser, "b", 0, "'a' expected");
    assertSuccess(parser, "a", new SeparatedList<>(Collections.singletonList('a')), 1);
    assertSuccess(parser, "a,a", new SeparatedList<>(
        Arrays.asList('a', 'a'), Collections.singletonList(',')), 3);
    assertSuccess(parser, "a,a,a", new SeparatedList<>(
        Arrays.asList('a', 'a', 'a'), Arrays.asList(',', ',')), 5);
    assertSuccess(parser, "a,b", new SeparatedList<>(Collections.singletonList('a')), 1);
    assertSuccess(parser, "a,", new SeparatedList<>(Collections.singletonList('a')), 1);
  }

  @Test
  public void testTimesSeparated() {
    Parser parser = element.timesSeparated(separator, 2);
    assertTrue(parser instanceof SeparatedRepeatingParser);

    assertFailure(parser, "", 0, "'a' expected");
    assertFailure(parser, "a", 1, "',' expected");
    assertFailure(parser, "a,", 2, "'a' expected");
    assertSuccess(parser, "a,a", new SeparatedList<>(
        Arrays.asList('a', 'a'), Collections.singletonList(',')), 3);
    assertSuccess(parser, "a,a,a", new SeparatedList<>(
        Arrays.asList('a', 'a'), Collections.singletonList(',')), 3);
  }

  @Test
  public void testRepeatSeparated() {
    Parser parser = element.repeatSeparated(separator, 2, 3);
    assertTrue(parser instanceof SeparatedRepeatingParser);

    assertFailure(parser, "", 0, "'a' expected");
    assertFailure(parser, "a", 1, "',' expected");
    assertSuccess(parser, "a,a", new SeparatedList<>(
        Arrays.asList('a', 'a'), Collections.singletonList(',')), 3);
    assertSuccess(parser, "a,a,a", new SeparatedList<>(
        Arrays.asList('a', 'a', 'a'), Arrays.asList(',', ',')), 5);
    assertSuccess(parser, "a,a,a,a", new SeparatedList<>(
        Arrays.asList('a', 'a', 'a'), Arrays.asList(',', ',')), 5);
  }

  @Test
  public void testFastParse() {
    Parser parser = element.plusSeparated(separator);
    assertEquals(-1, parser.fastParseOn("", 0));
    assertEquals(-1, parser.fastParseOn("b", 0));
    assertEquals(1, parser.fastParseOn("a", 0));
    assertEquals(3, parser.fastParseOn("a,a", 0));
    assertEquals(5, parser.fastParseOn("a,a,a", 0));
    assertEquals(1, parser.fastParseOn("a,b", 0));
    assertEquals(1, parser.fastParseOn("a,", 0));

    Parser bounded = element.repeatSeparated(separator, 2, 3);
    assertEquals(-1, bounded.fastParseOn("a", 0));
    assertEquals(3, bounded.fastParseOn("a,a", 0));
    assertEquals(5, bounded.fastParseOn("a,a,a", 0));
    assertEquals(5, bounded.fastParseOn("a,a,a,a", 0));
  }

  @Test
  public void testChildrenAndReplace() {
    SeparatedRepeatingParser parser = (SeparatedRepeatingParser) element.plusSeparated(separator);
    assertEquals(Arrays.asList(element, separator), parser.getChildren());

    Parser newElement = CharacterParser.of('b');
    Parser newSeparator = CharacterParser.of(';');

    parser.replace(element, newElement);
    assertEquals(newElement, parser.getChildren().get(0));

    parser.replace(separator, newSeparator);
    assertSame(newSeparator, parser.getSeparator());

    parser.setSeparator(separator);
    assertSame(separator, parser.getSeparator());
  }

  @Test
  public void testCopyAndEquality() {
    SeparatedRepeatingParser parser = (SeparatedRepeatingParser) element.plusSeparated(separator);
    SeparatedRepeatingParser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    assertFalse(parser.isEqualTo(element.starSeparated(separator)));
    assertFalse(parser.isEqualTo(element.plusSeparated(CharacterParser.of(';'))));
    assertFalse(parser.isEqualTo(CharacterParser.of('b').plusSeparated(separator)));
  }

  @Test(expected = NullPointerException.class)
  public void testNullSeparator() {
    new SeparatedRepeatingParser(element, null, 1, 2);
  }

  @Test(expected = NullPointerException.class)
  public void testSetNullSeparator() {
    SeparatedRepeatingParser parser = (SeparatedRepeatingParser) element.plusSeparated(separator);
    parser.setSeparator(null);
  }

  @Test
  public void testBackwardCompatibilitySeparatedBy() {
    Parser parser = element.separatedBy(separator);
    assertSuccess(parser, "a,a,a", Arrays.asList('a', ',', 'a', ',', 'a'), 5);

    Parser delimited = element.delimitedBy(separator);
    assertSuccess(delimited, "a,a,", Arrays.asList('a', ',', 'a', ','), 4);
  }
}
