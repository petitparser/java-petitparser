package org.petitparser.utils;

import org.junit.Test;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.UnicodeCharacterParser;
import org.petitparser.parser.repeating.RepeatingCharacterParser;
import org.petitparser.utils.optimizer.CharacterRepeaterRule;
import org.petitparser.utils.optimizer.FlattenChoiceRule;
import org.petitparser.utils.optimizer.OptimizeRule;
import org.petitparser.utils.optimizer.RemoveDelegateRule;
import org.petitparser.utils.optimizer.RemoveDuplicateRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.lowerCase;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests {@link Optimizer}.
 */
public class OptimizerTest {

  @Test
  public void testNoOptimization() {
    Parser input = lowerCase().settable().star();
    Parser output = new Optimizer().transform(input);
    assertTrue(output.isEqualTo(input));
  }

  @Test
  public void testRemoveBasicDelegates() {
    Parser input = lowerCase().settable();
    Parser output = new Optimizer().removeDelegates().transform(input);
    assertTrue(output.isEqualTo(lowerCase()));
  }

  @Test
  public void testRemoveNestedDelegates() {
    Parser input = lowerCase().settable().star();
    Parser output = new Optimizer().removeDelegates().transform(input);
    assertTrue(output.isEqualTo(lowerCase().star()));
  }

  @Test
  public void testRemoveDoubleDelegates() {
    Parser input = lowerCase().settable().settable();
    Parser output = new Optimizer().removeDelegates().transform(input);
    assertTrue(output.isEqualTo(lowerCase()));
  }

  @Test
  public void testRemoveDuplicates() {
    Parser input = lowerCase().seq(lowerCase());
    Parser output = new Optimizer().removeDuplicates().transform(input);
    assertTrue(input.isEqualTo(output));
    assertNotEquals(input.getChildren().get(0), input.getChildren().get(1));
    assertEquals(output.getChildren().get(0), output.getChildren().get(1));
  }

  @Test
  public void testRemoveDuplicatesWithCustomSet() {
    Set<Parser> customSet = new HashSet<>();
    Parser firstLower = lowerCase();
    customSet.add(firstLower);

    Parser input = lowerCase().seq(lowerCase());
    Parser output = new Optimizer().removeDuplicates(customSet).transform(input);
    assertTrue(input.isEqualTo(output));
    assertSame(firstLower, output.getChildren().get(0));
    assertSame(firstLower, output.getChildren().get(1));
  }

  @Test
  public void testFlattenChoiceNestedRight() {
    Parser a = of('a');
    Parser b = of('b');
    Parser c = of('c');

    // [a, [b, c]]
    Parser input = a.or(b.or(c));
    Parser output = new Optimizer().flattenChoices().transform(input);

    assertTrue(output instanceof ChoiceParser);
    assertEquals(3, output.getChildren().size());
    assertTrue(output.getChildren().get(0).isEqualTo(a));
    assertTrue(output.getChildren().get(1).isEqualTo(b));
    assertTrue(output.getChildren().get(2).isEqualTo(c));
  }

  @Test
  public void testFlattenChoiceNestedLeft() {
    Parser a = of('a');
    Parser b = of('b');
    Parser c = of('c');

    // ChoiceParser([ChoiceParser([a, b]), c])
    Parser input = new ChoiceParser(new ChoiceParser(a, b), c);
    Parser output = new Optimizer().flattenChoices().transform(input);

    assertTrue(output instanceof ChoiceParser);
    assertEquals(3, output.getChildren().size());
    assertTrue(output.getChildren().get(0).isEqualTo(a));
    assertTrue(output.getChildren().get(1).isEqualTo(b));
    assertTrue(output.getChildren().get(2).isEqualTo(c));
  }

  @Test
  public void testFlattenChoiceDeeplyNested() {
    Parser a = of('a');
    Parser b = of('b');
    Parser c = of('c');
    Parser d = of('d');

    // [a, [b, [c, d]]]
    Parser input = a.or(b.or(c.or(d)));
    Parser output = new Optimizer().flattenChoices().transform(input);

    assertTrue(output instanceof ChoiceParser);
    assertEquals(4, output.getChildren().size());
    assertTrue(output.getChildren().get(0).isEqualTo(a));
    assertTrue(output.getChildren().get(1).isEqualTo(b));
    assertTrue(output.getChildren().get(2).isEqualTo(c));
    assertTrue(output.getChildren().get(3).isEqualTo(d));
  }

  @Test
  public void testFlattenChoicePreservesFailureJoiner() {
    FailureJoiner joiner = new FailureJoiner.SelectFarthest();
    Parser a = of('a');
    Parser b = of('b');
    Parser c = of('c');

    Parser input = new ChoiceParser(joiner, a, new ChoiceParser(joiner, b, c));
    Parser output = new Optimizer().flattenChoices().transform(input);

    assertTrue(output instanceof ChoiceParser);
    assertSame(joiner, ((ChoiceParser) output).getFailureJoiner());
    assertEquals(3, output.getChildren().size());
  }

  @Test
  public void testFlattenChoiceAlreadyFlat() {
    Parser input = new ChoiceParser(of('a'), of('b'), of('c'));
    Parser output = new Optimizer().flattenChoices().transform(input);

    assertTrue(output.isEqualTo(input));
    assertEquals(3, output.getChildren().size());
  }

  @Test
  public void testCharacterRepeaterStar() {
    Parser input = of('a').star().flatten();
    Parser output = new Optimizer().characterRepeaters().transform(input);

    assertTrue(output instanceof RepeatingCharacterParser);
    assertTrue(output.isEqualTo(of('a').starString()));
    assertEquals("aaa", output.parse("aaa").get());
    assertTrue(output.accept("aaa"));
  }

  @Test
  public void testCharacterRepeaterPlus() {
    Parser input = digit().plus().flatten();
    Parser output = new Optimizer().characterRepeaters().transform(input);

    assertTrue(output instanceof RepeatingCharacterParser);
    assertTrue(output.isEqualTo(digit().plusString()));
    assertEquals("123", output.parse("123").get());
    Result failure = output.parse("abc");
    assertTrue(failure.isFailure());
  }

  @Test
  public void testCharacterRepeaterTimes() {
    Parser input = lowerCase().repeat(3, 3).flatten();
    Parser output = new Optimizer().characterRepeaters().transform(input);

    assertTrue(output instanceof RepeatingCharacterParser);
    assertTrue(output.isEqualTo(lowerCase().timesString(3)));
    assertEquals("abc", output.parse("abc").get());
    assertTrue(output.parse("ab").isFailure());
  }

  @Test
  public void testCharacterRepeaterCustomMessage() {
    Parser input = of('a').plus().flatten("custom error message");
    Parser output = new Optimizer().optimizeCharacterRepeaters().transform(input);

    assertTrue(output instanceof RepeatingCharacterParser);
    assertEquals("custom error message", ((RepeatingCharacterParser) output).getMessage());
    Result failure = output.parse("b");
    assertTrue(failure.isFailure());
    assertEquals("custom error message", failure.getMessage());
  }

  @Test
  public void testCharacterRepeaterUnicode() {
    Parser charParser = CharacterParser.any(true);
    assertTrue(charParser instanceof UnicodeCharacterParser);

    Parser input = charParser.star().flatten();
    Parser output = new Optimizer().characterRepeaters().transform(input);

    assertTrue(output instanceof RepeatingCharacterParser);
    RepeatingCharacterParser repeater = (RepeatingCharacterParser) output;
    assertTrue(repeater.isUnicode());
    assertEquals("hello", output.parse("hello").get());
  }

  @Test
  public void testCharacterRepeaterNonMatching() {
    // Flatten over a sequence (not a repeating parser)
    Parser input = of('a').seq(of('b')).flatten();
    Parser output = new Optimizer().characterRepeaters().transform(input);
    assertTrue(output.isEqualTo(input));

    // Flatten over possessive repeating of non-character parser
    Parser nonCharRepeating = org.petitparser.parser.primitive.StringParser.of("ab").star().flatten();
    Parser output2 = new Optimizer().characterRepeaters().transform(nonCharRepeating);
    assertTrue(output2.isEqualTo(nonCharRepeating));
  }

  @Test
  public void testAllOptimizations() {
    // A grammar with settable delegates, nested choices, and flatten repeating characters
    SettableParser settable = SettableParser.undefined();
    Parser a = of('a');
    Parser b = of('b').star().flatten();
    Parser c = of('c');
    settable.set(b);

    Parser input = a.or(settable.or(c));
    Parser output = new Optimizer().all().transform(input);

    assertTrue(output instanceof ChoiceParser);
    assertEquals(3, output.getChildren().size());
    assertTrue(output.getChildren().get(0).isEqualTo(a));
    assertTrue(output.getChildren().get(1) instanceof RepeatingCharacterParser);
    assertTrue(output.getChildren().get(2).isEqualTo(c));
  }

  @Test
  public void testStaticOptimize() {
    Parser input = of('a').or(of('b').or(of('c')));
    Parser output = Optimizer.optimize(input);

    assertTrue(output instanceof ChoiceParser);
    assertEquals(3, output.getChildren().size());
  }

  @Test
  public void testAddGenericFunctionTransformer() {
    Optimizer optimizer = new Optimizer();
    optimizer.add(p -> p instanceof CharacterParser ? of('z') : p);

    Parser input = of('a');
    Parser output = optimizer.transform(input);
    assertTrue(output.isEqualTo(of('z')));
  }

  @Test
  public void testAddRulesVarargsAndIterable() {
    Optimizer optimizer = new Optimizer();
    optimizer.add(new RemoveDelegateRule(), new FlattenChoiceRule());
    assertEquals(2, optimizer.getRules().size());

    Optimizer optimizer2 = new Optimizer();
    optimizer2.addAll(List.of(new CharacterRepeaterRule(), new RemoveDuplicateRule()));
    assertEquals(2, optimizer2.getRules().size());
  }
}
