package org.petitparser.utils.optimizer;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Failure;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.parser.primitive.UnicodeCharacterParser;
import org.petitparser.parser.repeating.PossessiveRepeatingParser;
import org.petitparser.parser.repeating.RepeatingCharacterParser;
import org.petitparser.utils.FailureJoiner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.lowerCase;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Parallel unit tests for the {@code org.petitparser.utils.optimizer} package.
 */
public class OptimizerTest {

  @Test
  public void testOptimizeRuleInterface() {
    OptimizeRule rule = parser -> parser.settable();
    assertTrue(rule.getName().contains("Lambda"));
    assertEquals("RemoveDelegateRule", new RemoveDelegateRule().getName());

    Parser original = of('a');
    Parser optimized = rule.optimize(original);
    assertTrue(optimized instanceof SettableParser);

    // Test OptimizeRule.of adaptation
    OptimizeRule adapted = OptimizeRule.of(p -> of('z'));
    assertTrue(adapted.apply(of('a')).isEqualTo(of('z')));
    assertSame(adapted, OptimizeRule.of(adapted));
  }

  @Test
  public void testRemoveDelegateRuleDirect() {
    RemoveDelegateRule rule = new RemoveDelegateRule();
    assertNull(rule.apply(null));

    Parser target = of('a');
    SettableParser s1 = SettableParser.with(target);
    SettableParser s2 = SettableParser.with(s1);
    DelegateParser d1 = new DelegateParser(s2);

    assertSame(target, rule.apply(d1));
    assertSame(target, rule.apply(s2));
    assertSame(target, rule.apply(target));

    // Backward compatibility alias RemoveDelegate
    RemoveDelegate alias = new RemoveDelegate();
    assertSame(target, alias.apply(d1));
  }

  @Test
  public void testRemoveDelegateRuleCycle() {
    RemoveDelegateRule rule = new RemoveDelegateRule();
    SettableParser s1 = SettableParser.undefined();
    SettableParser s2 = SettableParser.undefined();
    s1.set(s2);
    s2.set(s1);

    // Should not infinite loop; returns one of the cycle nodes safely
    Parser result = rule.apply(s1);
    assertNotNull(result);
  }

  @Test
  public void testRemoveDuplicateRuleDirect() {
    RemoveDuplicateRule rule = new RemoveDuplicateRule();
    assertNull(rule.apply(null));

    Parser a1 = of('a');
    Parser a2 = of('a');
    assertNotEquals(System.identityHashCode(a1), System.identityHashCode(a2));

    Parser first = rule.apply(a1);
    assertSame(a1, first);
    assertEquals(1, rule.getUniques().size());

    Parser second = rule.apply(a2);
    assertSame(a1, second);

    rule.clear();
    assertEquals(0, rule.getUniques().size());

    // Backward compatibility alias RemoveDuplicate
    RemoveDuplicate alias = new RemoveDuplicate(new HashSet<>());
    assertSame(a1, alias.apply(a1));
    assertSame(a1, alias.apply(a2));
  }

  @Test
  public void testFlattenChoiceRuleDirect() {
    FlattenChoiceRule rule = new FlattenChoiceRule();
    assertNull(rule.apply(null));

    Parser nonChoice = of('a');
    assertSame(nonChoice, rule.apply(nonChoice));

    Parser a = of('a');
    Parser b = of('b');
    Parser c = of('c');

    ChoiceParser flatChoice = new ChoiceParser(a, b);
    assertSame(flatChoice, rule.apply(flatChoice));

    ChoiceParser nestedChoice = new ChoiceParser(a, new ChoiceParser(b, c));
    Parser flattened = rule.apply(nestedChoice);
    assertTrue(flattened instanceof ChoiceParser);
    assertEquals(3, flattened.getChildren().size());
    assertSame(a, flattened.getChildren().get(0));
    assertSame(b, flattened.getChildren().get(1));
    assertSame(c, flattened.getChildren().get(2));
  }

  @Test
  public void testFlattenChoiceRuleWithJoiner() {
    FlattenChoiceRule rule = new FlattenChoiceRule();
    FailureJoiner joiner = new FailureJoiner.SelectFirst();
    ChoiceParser nested = new ChoiceParser(joiner, of('a'), new ChoiceParser(joiner, of('b'), of('c')));
    ChoiceParser flattened = (ChoiceParser) rule.apply(nested);

    assertSame(joiner, flattened.getFailureJoiner());
    assertEquals(3, flattened.getChildren().size());
  }

  @Test
  public void testCharacterRepeaterRuleDirect() {
    CharacterRepeaterRule rule = new CharacterRepeaterRule();
    assertNull(rule.apply(null));

    Parser nonFlatten = of('a').star();
    assertSame(nonFlatten, rule.apply(nonFlatten));

    Parser seqFlatten = of('a').seq(of('b')).flatten();
    assertSame(seqFlatten, rule.apply(seqFlatten));

    Parser nonCharFlatten = StringParser.of("xyz").star().flatten();
    assertSame(nonCharFlatten, rule.apply(nonCharFlatten));

    // Normal CharacterParser
    Parser charFlatten = of('a').star().flatten();
    Parser rewritten = rule.apply(charFlatten);
    assertTrue(rewritten instanceof RepeatingCharacterParser);
    RepeatingCharacterParser repeater = (RepeatingCharacterParser) rewritten;
    assertEquals(0, repeater.getMin());
    assertEquals(-1, repeater.getMax());
    assertFalse(repeater.isUnicode());

    // With custom message
    Parser customMsg = of('b').plus().flatten("must have b's");
    RepeatingCharacterParser customRewritten = (RepeatingCharacterParser) rule.apply(customMsg);
    assertEquals("must have b's", customRewritten.getMessage());
    assertEquals(1, customRewritten.getMin());

    // Unicode
    Parser unicodeChar = CharacterParser.any(true);
    Parser unicodeFlatten = unicodeChar.times(2).flatten();
    RepeatingCharacterParser unicodeRewritten = (RepeatingCharacterParser) rule.apply(unicodeFlatten);
    assertTrue(unicodeRewritten.isUnicode());
    assertEquals(2, unicodeRewritten.getMin());
    assertEquals(2, unicodeRewritten.getMax());
  }

  @Test
  public void testOptimizerSubclass() {
    Optimizer optimizer = new Optimizer();
    assertSame(optimizer, optimizer.add(new RemoveDelegateRule()));
    assertSame(optimizer, optimizer.add(p -> p));
    assertSame(optimizer, optimizer.add(new FlattenChoiceRule(), new CharacterRepeaterRule()));
    assertSame(optimizer, optimizer.addAll(List.of(new RemoveDuplicateRule())));

    assertEquals(5, optimizer.getRules().size());

    Optimizer optimizer2 = new Optimizer()
        .removeDelegates()
        .flattenChoices()
        .characterRepeaters()
        .optimizeCharacterRepeaters()
        .removeDuplicates()
        .removeDuplicates(new HashSet<>());
    assertNotNull(optimizer2);

    Optimizer allOpt = new Optimizer().all();
    assertNotNull(allOpt);

    assertEquals(4, Optimizer.ALL_RULES.size());
    assertTrue(Optimizer.ALL_RULES.get(0) instanceof RemoveDelegateRule);
    assertTrue(Optimizer.ALL_RULES.get(1) instanceof FlattenChoiceRule);
    assertTrue(Optimizer.ALL_RULES.get(2) instanceof CharacterRepeaterRule);
    assertTrue(Optimizer.ALL_RULES.get(3) instanceof RemoveDuplicateRule);
  }

  @Test
  public void testFailureJoinerEqualsAndHashCode() {
    FailureJoiner.SelectFirst sf1 = new FailureJoiner.SelectFirst();
    FailureJoiner.SelectFirst sf2 = new FailureJoiner.SelectFirst();
    assertEquals(sf1, sf2);
    assertEquals(sf1.hashCode(), sf2.hashCode());
    assertNotEquals(sf1, new Object());

    FailureJoiner.SelectLast sl1 = new FailureJoiner.SelectLast();
    FailureJoiner.SelectLast sl2 = new FailureJoiner.SelectLast();
    assertEquals(sl1, sl2);
    assertEquals(sl1.hashCode(), sl2.hashCode());
    assertNotEquals(sl1, sf1);

    FailureJoiner.SelectFarthest sfarthest1 = new FailureJoiner.SelectFarthest();
    FailureJoiner.SelectFarthest sfarthest2 = new FailureJoiner.SelectFarthest();
    assertEquals(sfarthest1, sfarthest2);
    assertEquals(sfarthest1.hashCode(), sfarthest2.hashCode());
    assertNotEquals(sfarthest1, sl1);

    FailureJoiner.SelectFarthestJoined sfj1 = new FailureJoiner.SelectFarthestJoined(" OR ");
    FailureJoiner.SelectFarthestJoined sfj2 = new FailureJoiner.SelectFarthestJoined(" OR ");
    FailureJoiner.SelectFarthestJoined sfj3 = new FailureJoiner.SelectFarthestJoined(" AND ");
    assertEquals(sfj1, sfj2);
    assertEquals(sfj1.hashCode(), sfj2.hashCode());
    assertNotEquals(sfj1, sfj3);
    assertNotEquals(sfj1, sfarthest1);
  }

  @Test
  public void testChoiceParserHasEqualProperties() {
    FailureJoiner joiner1 = new FailureJoiner.SelectLast();
    FailureJoiner joiner2 = new FailureJoiner.SelectLast();
    FailureJoiner joiner3 = new FailureJoiner.SelectFirst();

    ChoiceParser c1 = new ChoiceParser(joiner1, of('a'), of('b'));
    ChoiceParser c2 = new ChoiceParser(joiner2, of('a'), of('b'));
    ChoiceParser c3 = new ChoiceParser(joiner3, of('a'), of('b'));

    assertTrue(c1.isEqualTo(c2));
    assertFalse(c1.isEqualTo(c3));
  }

  @Test
  public void testFailureJoinerEdgeCases() {
    FailureJoiner.SelectFirst sf = new FailureJoiner.SelectFirst();
    assertTrue(sf.equals(sf));
    assertFalse(sf.equals(null));
    assertFalse(sf.equals("other"));

    FailureJoiner.SelectLast sl = new FailureJoiner.SelectLast();
    assertTrue(sl.equals(sl));
    assertFalse(sl.equals(null));
    assertFalse(sl.equals("other"));

    FailureJoiner.SelectFarthest sfarthest = new FailureJoiner.SelectFarthest();
    assertTrue(sfarthest.equals(sfarthest));
    assertFalse(sfarthest.equals(null));
    assertFalse(sfarthest.equals("other"));

    FailureJoiner.SelectFarthestJoined sfjDefault = new FailureJoiner.SelectFarthestJoined();
    FailureJoiner.SelectFarthestJoined sfjCustom = new FailureJoiner.SelectFarthestJoined(" OR ");
    assertTrue(sfjDefault.equals(sfjDefault));
    assertTrue(sfjDefault.equals(sfjCustom));
    assertFalse(sfjDefault.equals(null));
    assertFalse(sfjDefault.equals("other"));
  }

  @Test
  public void testFlattenChoiceCycle() {
    FlattenChoiceRule rule = new FlattenChoiceRule();
    ChoiceParser c1 = new ChoiceParser(of('a'), of('b'));
    // Simulate circular child reference via replace
    c1.replace(of('b'), c1);
    // Applying rule to a choice containing itself shouldn't infinite loop
    Parser result = rule.apply(c1);
    assertNotNull(result);
  }

  @Test
  public void testRulesSubpackageAliases() {
    org.petitparser.utils.optimizer.rules.RemoveDelegateRule r1 =
        new org.petitparser.utils.optimizer.rules.RemoveDelegateRule();
    org.petitparser.utils.optimizer.rules.RemoveDuplicateRule r2 =
        new org.petitparser.utils.optimizer.rules.RemoveDuplicateRule();
    org.petitparser.utils.optimizer.rules.FlattenChoiceRule r3 =
        new org.petitparser.utils.optimizer.rules.FlattenChoiceRule();
    org.petitparser.utils.optimizer.rules.CharacterRepeaterRule r4 =
        new org.petitparser.utils.optimizer.rules.CharacterRepeaterRule();

    assertNotNull(r1);
    assertNotNull(r2);
    assertNotNull(r3);
    assertNotNull(r4);
  }

  @Test
  public void testFlattenChoiceDifferentFailureJoinerDirect() {
    FlattenChoiceRule rule = new FlattenChoiceRule();
    FailureJoiner joinerFirst = new FailureJoiner.SelectFirst();
    FailureJoiner joinerLast = new FailureJoiner.SelectLast();

    ChoiceParser inner = new ChoiceParser(joinerFirst, of('b'), of('c'));
    ChoiceParser outer = new ChoiceParser(joinerLast, of('a'), inner);

    Parser result = rule.apply(outer);
    assertSame(outer, result);
    assertEquals(2, ((ChoiceParser) result).getChildren().size());
  }

  @Test
  public void testFlattenChoiceAllCyclesReturnsSame() {
    FlattenChoiceRule rule = new FlattenChoiceRule();
    ChoiceParser c1 = new ChoiceParser(of('a'), of('b'));
    c1.replace(of('a'), c1);
    c1.replace(of('b'), c1);

    Parser result = rule.apply(c1);
    assertSame(c1, result);
  }

  @Test
  public void testCharacterRepeaterUnwrapsDelegates() {
    CharacterRepeaterRule rule = new CharacterRepeaterRule();
    SettableParser s = SettableParser.with(of('a').star());
    Parser flatten = s.flatten();

    Parser rewritten = rule.apply(flatten);
    assertTrue(rewritten instanceof RepeatingCharacterParser);
    assertEquals("aaa", rewritten.parse("aaa").get());
  }

  @Test
  public void testCustomFlattenSubclassPreserved() {
    CharacterRepeaterRule rule = new CharacterRepeaterRule();
    Parser custom = new org.petitparser.parser.actions.FlattenParser(of('a').star()) {};
    Parser rewritten = rule.apply(custom);
    assertSame(custom, rewritten);
  }

  @Test
  public void testCustomChoiceSubclassPreserved() {
    FlattenChoiceRule rule = new FlattenChoiceRule();
    ChoiceParser custom = new ChoiceParser(of('a'), of('b')) {};
    ChoiceParser outer = new ChoiceParser(of('c'), custom);

    Parser result = rule.apply(outer);
    assertSame(outer, result);
  }

  @Test
  public void testRemoveDuplicateResetClearsDefaultPreservesCustom() {
    RemoveDuplicateRule defaultRule = new RemoveDuplicateRule();
    defaultRule.apply(of('a'));
    assertEquals(1, defaultRule.getUniques().size());
    defaultRule.reset();
    assertEquals(0, defaultRule.getUniques().size());

    Set<Parser> customSet = new HashSet<>();
    customSet.add(of('a'));
    RemoveDuplicateRule customRule = new RemoveDuplicateRule(customSet);
    assertEquals(1, customRule.getUniques().size());
    customRule.reset();
    assertEquals(1, customRule.getUniques().size());
  }

  @Test
  public void testOptimizerSubclassConstructors() {
    Optimizer opt1 = new Optimizer(new RemoveDelegateRule(), new FlattenChoiceRule());
    assertEquals(2, opt1.getRules().size());

    Optimizer opt2 = new Optimizer(List.of(new CharacterRepeaterRule()));
    assertEquals(1, opt2.getRules().size());

    Optimizer opt3 = new Optimizer((Iterable<OptimizeRule>) null);
    assertEquals(0, opt3.getRules().size());
  }
}
