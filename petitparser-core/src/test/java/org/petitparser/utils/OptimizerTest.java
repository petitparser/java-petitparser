package org.petitparser.utils;

import org.junit.Test;
import org.petitparser.parser.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.lowerCase;

/**
 * Tests {@link Optimizer}.
 */
public class OptimizerTest {

  @Test
  public void testNoOptimization() {
    Parser input = lowerCase().settable().star();
    Parser output = new Optimizer()
        .transform(input);
    assertTrue(output.isEqualTo(input));
  }

  @Test
  public void testRemoveBasicDelegates() {
    Parser input = lowerCase().settable();
    Parser output = new Optimizer()
        .removeDelegates()
        .transform(input);
    assertTrue(output.isEqualTo(lowerCase()));
  }

  @Test
  public void testRemoveNestedDelegates() {
    Parser input = lowerCase().settable().star();
    Parser output = new Optimizer()
        .removeDelegates()
        .transform(input);
    assertTrue(output.isEqualTo(lowerCase().star()));
  }

  @Test
  public void testRemoveDoubleDelegates() {
    Parser input = lowerCase().settable().settable();
    Parser output = new Optimizer()
        .removeDelegates()
        .transform(input);
    assertTrue(output.isEqualTo(lowerCase()));
  }

  @Test
  public void testRemoveDuplicates() {
    Parser input = lowerCase().seq(lowerCase());
    Parser output = new Optimizer()
        .removeDuplicates()
        .transform(input);
    assertTrue(input.isEqualTo(output));
    assertNotEquals(input.getChildren().get(0), input.getChildren().get(1));
    assertEquals(output.getChildren().get(0), output.getChildren().get(1));
  }
}
