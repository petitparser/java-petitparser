package org.petitparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.petitparser.buffer.StringBuffer;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

public class ParserAssertions {

  private ParserAssertions() {
    // just a namespace
  }

  public static <T> void assertSuccess(Parser<T> parser, String input, T result) {
    assertSuccess(parser, input, result, input.length());
  }

  public static <T> void assertSuccess(Parser<T> parser, String input, T expected, int position) {
    Context context = new Context(new StringBuffer(input));
    Result<T> result = parser.parse(context);
    assertTrue("Expected parse success", result.isSuccess());
    assertEquals("Position", position, result.getPosition());
    assertEquals("Result", expected, result.get());
  }

  public static <T> void assertFailure(Parser<T> parser, String input) {
    Context context = new Context(new StringBuffer(input));
    Result<T> result = parser.parse(context);
    assertTrue("Expected parse failure", result.isFailure());
    assertEquals("Position", 0, result.getPosition());
  }

}
