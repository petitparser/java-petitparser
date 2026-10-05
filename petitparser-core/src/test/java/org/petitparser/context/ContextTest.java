package org.petitparser.context;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link Context}.
 */
public class ContextTest {

  @Test
  public void testContextPositionString() {
    Context context = new Context("a\nb", 2);
    assertEquals("a\nb", context.getBuffer());
    assertEquals(2, context.getPosition());
    assertEquals("2:1", context.toPositionString());
    assertEquals("Context[2:1]", context.toString());
  }

  @Test
  public void testSuccessAndFailure() {
    Context context = new Context("a\nb", 2);
    Success success = context.success("ok");
    assertTrue(success.isSuccess());
    assertEquals("ok", success.get());
    assertEquals(2, success.getPosition());
    assertEquals("Success[2:1]: ok", success.toString());

    Failure failure = context.failure("error");
    assertTrue(failure.isFailure());
    assertEquals("error", failure.getMessage());
    assertEquals(2, failure.getPosition());
    assertEquals("Failure[2:1]: error", failure.toString());
  }
}
