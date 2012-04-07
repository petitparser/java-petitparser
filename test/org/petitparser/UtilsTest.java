package org.petitparser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.petitparser.parser.CharParser;
import org.petitparser.parser.DelegateParser;
import org.petitparser.parser.Parser;
import org.petitparser.utils.Queries;
import org.petitparser.utils.Transformations;

import com.google.common.collect.Iterables;

/**
 * Tests for {@link Queries} and {@link Transformations}.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class UtilsTest {

  @Test
  public void testSimpleQuery() {
    Parser parser1 = Chars.lowerCase();
    Parser[] parsers = Iterables.toArray(Queries.iterable(parser1),
        Parser.class);
    assertArrayEquals(parsers, new Parser[] { parser1 });
  }

  @Test
  public void testNestedQuery() {
    Parser parser3 = Chars.lowerCase();
    Parser parser2 = parser3.star();
    Parser parser1 = parser2.flatten();
    Parser[] parsers = Iterables.toArray(Queries.iterable(parser1),
        Parser.class);
    assertArrayEquals(parsers, new Parser[] { parser1, parser2, parser3 });
  }

  @Test
  public void testBranchedQuery() {
    Parser parser3 = Chars.lowerCase();
    Parser parser2 = Chars.upperCase();
    Parser parser1 = parser2.seq(parser3);
    Parser[] parsers = Iterables.toArray(Queries.iterable(parser1),
        Parser.class);
    assertArrayEquals(parsers, new Parser[] { parser1, parser3, parser2 });
  }

  @Test
  public void testLoopingQuery() {
    DelegateParser parser3 = new DelegateParser();
    DelegateParser parser2 = new DelegateParser();
    DelegateParser parser1 = new DelegateParser();
    parser1.replace(parser1.getDelegate(), parser2);
    parser2.replace(parser2.getDelegate(), parser3);
    parser3.replace(parser3.getDelegate(), parser1);
    Parser[] parsers = Iterables.toArray(Queries.iterable(parser1),
        Parser.class);
    assertArrayEquals(parsers, new Parser[] { parser1, parser2, parser3 });
  }

  @Test
  public void testNextAtEndShouldThrowAnException() {
    Parser parser = Chars.lowerCase();
    Iterator<Parser> iterator = Queries.iterator(parser);
    assertEquals(parser, iterator.next());
    assertFalse(iterator.hasNext());
    try {
      iterator.next();
    } catch (NoSuchElementException e) {
      return;
    }
    fail();
  }

  @Test
  public void testRemoveShouldNotBeSupported() {
    Parser parser = Chars.lowerCase();
    Iterator<Parser> iterator = Queries.iterator(parser);
    try {
      iterator.remove();
    } catch (UnsupportedOperationException e) {
      return;
    }
    fail();
  }

  @Test
  public void testRemoveDelegate() {
    Parser parser2 = Chars.lowerCase();
    Parser parser1 = new DelegateParser(parser2);
    Parser transformed = Transformations.removeDelegates(parser1);
    assertEquals(CharParser.class, transformed.getClass());
  }

}
