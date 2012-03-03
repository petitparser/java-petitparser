package org.petitparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.petitparser.buffer.StringBuffer;
import org.petitparser.context.Context;

public class ParserAssertions {

  private ParserAssertions() {

  }

  public static <T> void assertParse(Parser<T> parser, String input, T result) {
    assertParse(parser, input, result, input.length());
  }

  public static <T> void assertParse(Parser<T> parser, String input, T result,
      int position) {
    Context<T> context = new Context<T>(new StringBuffer(input), 0);
    context = parser.parse(context);
    assertTrue(context.isSuccess());
    assertFalse(context.isFailure());
    assertEquals(position, context.getPosition());
    assertEquals(result, context.get());
  }

  public static <T> void assertFail(Parser<T> parser, String input) {
    Context<T> context = new Context<T>(new StringBuffer(input), 0);
    context = parser.parse(context);
    assertFalse(context.isSuccess());
    assertTrue(context.isFailure());
    assertEquals(0, context.getPosition());
  }

}
