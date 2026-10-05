package org.petitparser.parser.actions;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link ContinuationParser}.
 */
public class ContinuationParserTest {

  @Test(expected = NullPointerException.class)
  public void testNullHandler() {
    new ContinuationParser(CharacterParser.digit(), null);
  }

  @Test
  public void testFastParseSuccessfulContinuation() {
    Parser parser = CharacterParser.digit()
        .callCC((continuation, context) -> context.success("Always succeed"));

    // parseOn succeeds on 'a' even though digit() fails
    assertTrue(parser.parse("a").isSuccess());
    assertEquals(0, parser.parse("a").getPosition());

    // fastParseOn must also respect the handler and succeed
    assertEquals(0, parser.fastParseOn("a", 0));
    assertTrue(parser.accept("a"));
  }

  @Test
  public void testFastParseFailingContinuation() {
    Parser parser = CharacterParser.digit()
        .callCC((continuation, context) -> context.failure("Always fail"));

    // parseOn fails on '1' even though digit() succeeds
    assertFalse(parser.parse("1").isSuccess());

    // fastParseOn must also respect the handler and fail
    assertEquals(-1, parser.fastParseOn("1", 0));
    assertFalse(parser.accept("1"));
  }

  @Test
  public void testFastParseSideEffectsInContinuation() {
    AtomicInteger invocationCount = new AtomicInteger(0);
    Parser parser = CharacterParser.digit().callCC((continuation, context) -> {
      invocationCount.incrementAndGet();
      return continuation.apply(context);
    });

    assertEquals(1, parser.fastParseOn("1", 0));
    assertEquals(1, invocationCount.get());

    assertEquals(-1, parser.fastParseOn("a", 0));
    assertEquals(2, invocationCount.get());
  }

  @Test
  public void testContinuationRedirecting() {
    Parser parser = CharacterParser.digit().callCC(
        (continuation, context) -> CharacterParser.letter().parseOn(context));

    assertFalse(parser.parse("1").isSuccess());
    assertTrue(parser.parse("a").isSuccess());

    assertEquals(-1, parser.fastParseOn("1", 0));
    assertEquals(1, parser.fastParseOn("a", 0));
    assertFalse(parser.accept("1"));
    assertTrue(parser.accept("a"));
  }

  @Test
  public void testContinuationResuming() {
    List<Function<Context, Result>> continuations = new ArrayList<>();
    List<Context> contexts = new ArrayList<>();
    Parser parser = CharacterParser.digit().callCC((continuation, context) -> {
      continuations.add(continuation);
      contexts.add(context);
      return context.failure("Abort");
    });

    assertFalse(parser.parse("1").isSuccess());
    assertFalse(parser.parse("a").isSuccess());

    assertTrue(continuations.get(0).apply(contexts.get(0)).isSuccess());
    assertFalse(continuations.get(1).apply(contexts.get(1)).isSuccess());
  }

  @Test
  public void testCopyAndEquality() {
    ContinuationParser.ContinuationHandler handler = (c, ctx) -> c.apply(ctx);
    ContinuationParser parser1 = new ContinuationParser(CharacterParser.digit(), handler);
    ContinuationParser parser2 = new ContinuationParser(CharacterParser.digit(), handler);
    ContinuationParser parser3 = new ContinuationParser(CharacterParser.letter(), handler);

    assertTrue(parser1.isEqualTo(parser2));
    assertFalse(parser1.isEqualTo(parser3));

    ContinuationParser copy = parser1.copy();
    assertNotSame(parser1, copy);
    assertTrue(parser1.isEqualTo(copy));

    Parser child = parser1.getChildren().get(0);
    Parser newChild = CharacterParser.letter();
    parser1.replace(child, newChild);
    assertEquals(newChild, parser1.getChildren().get(0));
  }
}
