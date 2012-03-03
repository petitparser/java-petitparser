package org.petitparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.petitparser.buffer.StringBuffer;
import org.petitparser.context.Context;

public class ParserAssertions {

  private ParserAssertions() {

  }

  public static <T> void assertSuccess(Parser<T> parser, String input, T result) {
    assertSuccess(parser, input, result, input.length());
  }

  public static <T> void assertSuccess(Parser<T> parser, String input,
      T result, int position) {
    Context<T> context = new Context<T>(new StringBuffer(input));
    context = parser.parse(context);
    assertTrue("Expected parse success", context.isSuccess());
    assertEquals("Position", position, context.getPosition());
    assertEquals("Result", result, context.get());
  }

  public static <T> void assertFailure(Parser<T> parser, String input) {
    Context<T> context = new Context<T>(new StringBuffer(input));
    context = parser.parse(context);
    assertTrue("Expected parse failure", context.isFailure());
    assertEquals("Position", 0, context.getPosition());
  }

}
