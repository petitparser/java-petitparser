package org.petitparser;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;
import org.petitparser.utils.Optimizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Optimizer}.
 */
public class OptimizerTest {

  @Test
  public void testRemoveBasicDelegates() {
    Parser input = CharacterParser.lowerCase().settable();
    Parser output = new Optimizer()
        .addDelegateRemoval()
        .transform(input);
    assertTrue(output.isEqualTo(CharacterParser.lowerCase()));
  }

  @Test
  public void testRemoveNestedDelegates() {
    Parser input = CharacterParser.lowerCase().settable().star();
    Parser output = new Optimizer()
        .addDelegateRemoval()
        .transform(input);
    assertTrue(output.isEqualTo(CharacterParser.lowerCase().star()));
  }

  @Test
  public void testRemoveDoubleDelegates() {
    Parser input = CharacterParser.lowerCase().settable().settable();
    Parser output = new Optimizer()
        .addDelegateRemoval()
        .transform(input);
    assertTrue(output.isEqualTo(CharacterParser.lowerCase()));
  }

  @Test
  public void testRemoveDuplicates() {
    Parser input = CharacterParser.lowerCase().seq(CharacterParser.lowerCase());
    Parser output = new Optimizer()
        .addDuplicateRemoval()
        .transform(input);
    assertTrue(input.isEqualTo(output));
    assertNotEquals(input.getChildren().get(0), input.getChildren().get(1));
    assertEquals(output.getChildren().get(0), output.getChildren().get(1));
  }
}
