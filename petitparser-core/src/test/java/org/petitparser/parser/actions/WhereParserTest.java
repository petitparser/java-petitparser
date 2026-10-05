package org.petitparser.parser.actions;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.any;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests for {@link WhereParser}.
 */
public class WhereParserTest {

  @Test
  public void testDefault() {
    Parser parser = any().where(c -> Objects.equals(c, '*'));
    assertSuccess(parser, "*", '*');
    assertFailure(parser, "", 0, "any character expected");
    assertFailure(parser, "!", 0, "unexpected \"!\"");
  }

  @Test
  public void testWithMessage() {
    Parser parser = any().where(c -> Objects.equals(c, '*'), "star expected");
    assertSuccess(parser, "*", '*');
    assertFailure(parser, "", 0, "any character expected");
    assertFailure(parser, "!", 0, "star expected");
  }

  @Test
  public void testWithFactory() {
    BiFunction<Context, Result, Result> factory =
        (context, success) -> context.failure(success.get() + " is not divisible by 7");
    Parser parser = digit().plus().flatten()
        .map((String s) -> Integer.parseInt(s))
        .where((Integer v) -> v % 7 == 0, factory);
    assertSuccess(parser, "7", 7);
    assertSuccess(parser, "14", 14);
    assertSuccess(parser, "861", 861);
    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "865", 0, "865 is not divisible by 7");
  }

  @Test
  public void testSubsequencePosition() {
    Parser parser = of('a').seq(of('b').where(c -> Objects.equals(c, 'x')));
    assertFailure(parser, "ab", 1, "unexpected \"b\"");
  }

  @Test
  public void testGetters() {
    Predicate<Object> predicate = Objects::nonNull;
    WhereParser<Object> parserDefault = new WhereParser<>(any(), predicate);
    assertSame(predicate, parserDefault.getPredicate());
    assertNull(parserDefault.getMessage());
    assertNull(parserDefault.getFailureFactory());

    WhereParser<Object> parser = new WhereParser<>(any(), predicate, "not null");
    assertSame(predicate, parser.getPredicate());
    assertEquals("not null", parser.getMessage());
    assertNull(parser.getFailureFactory());

    BiFunction<Context, Result, Result> factory = (ctx, res) -> ctx.failure("custom");
    WhereParser<Object> parserWithFactory = new WhereParser<>(any(), predicate, factory);
    assertSame(factory, parserWithFactory.getFailureFactory());
    assertNull(parserWithFactory.getMessage());
  }

  @Test
  public void testNullChecks() {
    try {
      new WhereParser<>(null, Objects::nonNull);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }

    try {
      new WhereParser<>(any(), null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }

    try {
      new WhereParser<>(any(), Objects::nonNull, (BiFunction<Context, Result, Result>) null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }
  }

  @Test
  public void testFastParse() {
    Parser parser = of('a').where(c -> Objects.equals(c, 'a'));
    assertEquals(1, parser.fastParseOn("a", 0));
    assertEquals(-1, parser.fastParseOn("b", 0));

    Parser failingPredicate = of('a').where(c -> false);
    assertEquals(-1, failingPredicate.fastParseOn("a", 0));
  }

  @Test
  public void testCopyAndEquality() {
    Predicate<Object> predicate = Objects::nonNull;
    Parser parser = any().where(predicate, "err");
    Parser copy = parser.copy();

    assertNotSame(parser, copy);
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));

    Parser differentDelegate = digit().where(predicate, "err");
    assertFalse(parser.isEqualTo(differentDelegate));

    Parser differentPredicate = any().where(c -> true, "err");
    assertFalse(parser.isEqualTo(differentPredicate));

    Parser differentMessage = any().where(predicate, "other");
    assertFalse(parser.isEqualTo(differentMessage));

    BiFunction<Context, Result, Result> factory = (ctx, res) -> ctx.failure("factory");
    Parser withFactory = any().where(predicate, factory);
    assertFalse(parser.isEqualTo(withFactory));

    Parser withFactoryCopy = withFactory.copy();
    assertTrue(withFactory.isEqualTo(withFactoryCopy));

    BiFunction<Context, Result, Result> otherFactory = (ctx, res) -> ctx.failure("other");
    Parser withOtherFactory = any().where(predicate, otherFactory);
    assertFalse(withFactory.isEqualTo(withOtherFactory));
  }

  @Test
  public void testToString() {
    Parser defaultParser = any().where(Objects::nonNull);
    assertEquals("WhereParser", defaultParser.toString());

    Parser messageParser = any().where(Objects::nonNull, "custom");
    assertEquals("WhereParser[custom]", messageParser.toString());
  }

  @Test
  public void testFastParseNonZeroOffset() {
    Parser parser = of('a').where(c -> Objects.equals(c, 'a'));
    assertEquals(2, parser.fastParseOn("ba", 1));
    assertEquals(-1, parser.fastParseOn("bb", 1));
  }
}
