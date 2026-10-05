package org.petitparser.utils.linter;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.EpsilonParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.utils.Analyzer;
import org.petitparser.utils.Linter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.petitparser.parser.primitive.CharacterParser.any;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.newline;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.EpsilonParser.epsilon;
import static org.petitparser.parser.primitive.StringParser.of;

/**
 * Unit tests for {@link Linter} and all 13 {@link LinterRule} implementations.
 */
public class LinterTest {

  // --------------------------------------------------------------------------
  // Architecture Tests
  // --------------------------------------------------------------------------

  @Test
  public void testLinterTypeEnum() {
    assertEquals(3, LinterType.values().length);
    assertEquals(LinterType.INFO, LinterType.valueOf("INFO"));
    assertEquals(LinterType.WARNING, LinterType.valueOf("WARNING"));
    assertEquals(LinterType.ERROR, LinterType.valueOf("ERROR"));
  }

  @Test
  public void testLinterRuleBase() {
    AtomicBoolean called = new AtomicBoolean(false);
    LinterRule rule = LinterRule.of(LinterType.WARNING, "Custom", (r, a, p, cb) -> {
      called.set(true);
      cb.accept(new LinterIssue(r, p, "Custom issue"));
    });

    assertEquals(LinterType.WARNING, rule.getType());
    assertEquals("Custom", rule.getTitle());
    assertTrue(rule.toString().contains("Custom"));

    Parser parser = of('a');
    List<LinterIssue> issues = Linter.lint(parser, List.of(rule));
    assertTrue(called.get());
    assertEquals(1, issues.size());
    assertEquals("Custom issue", issues.get(0).getDescription());
    assertEquals(LinterType.WARNING, issues.get(0).getType());
    assertEquals("Custom", issues.get(0).getTitle());
    assertSame(parser, issues.get(0).getParser());
    assertSame(rule, issues.get(0).getRule());

    // Equality and hashCode of LinterRule
    LinterRule sameRule = LinterRule.of(LinterType.WARNING, "Custom", (r, a, p, cb) -> {});
    assertEquals(rule, sameRule);
    assertNotEquals(rule, LinterRule.of(LinterType.ERROR, "Custom", (r, a, p, cb) -> {}));
    assertNotEquals(rule, LinterRule.of(LinterType.WARNING, "Other", (r, a, p, cb) -> {}));
    assertEquals(new CharacterRepeaterRule(), new CharacterRepeaterRule());
    assertEquals(new CharacterRepeaterRule().hashCode(), new CharacterRepeaterRule().hashCode());
    assertNotEquals(new CharacterRepeaterRule(), new DuplicateParserRule());
    assertNotEquals(new CharacterRepeaterRule(), null);
    assertNotEquals(new CharacterRepeaterRule(), "other");
  }

  @Test
  public void testLinterIssueEqualityAndToString() {
    LinterRule rule1 = new CharacterRepeaterRule();
    LinterRule rule2 = new DuplicateParserRule();
    Parser p1 = of('a');
    Parser p2 = of('b');

    LinterIssue issue1 = new LinterIssue(rule1, p1, "Desc");
    LinterIssue issue1Same = new LinterIssue(rule1, p1, "Desc");
    LinterIssue issue2 = new LinterIssue(rule2, p1, "Desc");
    LinterIssue issue3 = new LinterIssue(rule1, p2, "Desc");
    LinterIssue issue4 = new LinterIssue(rule1, p1, "Other");

    assertEquals(issue1, issue1);
    assertEquals(issue1, issue1Same);
    assertEquals(issue1.hashCode(), issue1Same.hashCode());
    assertNotEquals(issue1, issue2);
    assertNotEquals(issue1, issue3);
    assertNotEquals(issue1, issue4);
    assertNotEquals(issue1, null);
    assertNotEquals(issue1, "string");

    assertTrue(issue1.toString().contains("LinterIssue"));
    assertTrue(issue1.toString().contains("Character repeater"));
    assertTrue(issue1.toString().contains("Desc"));
  }

  @Test
  public void testLinterFormatIterable() {
    List<String> items = List.of("alpha", "beta");
    assertEquals(" - alpha\n - beta", LinterRule.formatIterable(items));
    assertEquals(" 1: alpha\n 2: beta", LinterRule.formatIterable(items, 1));
    assertEquals("", LinterRule.formatIterable(Collections.emptyList()));
  }

  @Test
  public void testLinterIsParserIterableEqual() {
    Parser a1 = of('a');
    Parser a2 = of('a');
    Parser b1 = of('b');
    Parser b2 = of('b');

    assertTrue(LinterRule.isParserIterableEqual(List.of(a1, b1), List.of(b2, a2)));
    assertFalse(LinterRule.isParserIterableEqual(List.of(a1), List.of(a1, b1)));
    assertFalse(LinterRule.isParserIterableEqual(List.of(a1, b1), List.of(a1)));
    assertFalse(LinterRule.isParserIterableEqual(List.of(a1), List.of(b1)));
  }

  @Test
  public void testLinterEngineInvocationAndCallback() {
    Parser input = of('a').or(of('b'));
    Set<Parser> seen = new HashSet<>();
    LinterRule fakeRule = LinterRule.of(LinterType.ERROR, "Fake Rule", (rule, analyzer, parser, cb) -> {
      seen.add(parser);
    });

    List<LinterIssue> results = Linter.lint(input, List.of(fakeRule));
    assertTrue(results.isEmpty());
    assertEquals(3, seen.size());
    assertTrue(seen.contains(input));
    assertTrue(seen.contains(input.getChildren().get(0)));
    assertTrue(seen.contains(input.getChildren().get(1)));

    List<LinterIssue> callbackIssues = new ArrayList<>();
    LinterRule triggerRule = LinterRule.of(LinterType.ERROR, "Trigger Rule", (rule, analyzer, parser, cb) -> {
      if (parser == input) {
        cb.accept(new LinterIssue(rule, parser, "Triggered"));
      }
    });

    results = Linter.lint(input, callbackIssues::add, List.of(triggerRule));
    assertEquals(1, results.size());
    assertEquals(results, callbackIssues);
  }

  @Test
  public void testLinterDefaultRulesExclusions() {
    // Info rule issues are excluded by default in Linter.lint(parser)
    Parser input = of('a').or(of('b').or(of('c'))); // Nested choice is INFO
    List<LinterIssue> issues = Linter.lint(input);
    assertTrue(issues.isEmpty()); // NestedChoice is INFO, excluded by default

    // If we include all types via Query:
    issues = Linter.query(input).includeAllTypes().lint();
    assertFalse(issues.isEmpty());
    assertEquals("Nested choice", issues.get(0).getTitle());

    // Exclude specific rule title
    issues = Linter.query(input).includeAllTypes().excludeRule("Nested choice").lint();
    assertTrue(issues.isEmpty());

    // Exclude specific types
    issues = Linter.query(input).includeAllTypes().excludeType(LinterType.INFO).lint();
    assertTrue(issues.isEmpty());

    // Facade org.petitparser.utils.Linter
    assertEquals(0, org.petitparser.utils.Linter.lint(input).size());
    assertNotNull(org.petitparser.utils.Linter.query(input));
  }

  @Test
  public void testLinterQueryBuilder() {
    Parser input = of('a');
    List<LinterIssue> issues = Linter.query(input)
        .rules(new CharacterRepeaterRule(), new DuplicateParserRule())
        .excludeRules(List.of("Duplicate parser"))
        .excludeTypes(List.of(LinterType.INFO))
        .onIssue(issue -> {})
        .lint();
    assertTrue(issues.isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 1: CharacterRepeaterRule
  // --------------------------------------------------------------------------

  @Test
  public void testCharacterRepeaterRulePositive() {
    List<LinterRule> rules = List.of(new CharacterRepeaterRule());

    Parser p1 = of('a').star().flatten();
    List<LinterIssue> issues1 = Linter.lint(p1, rules);
    assertEquals(1, issues1.size());
    assertSame(p1, issues1.get(0).getParser());
    assertEquals(LinterType.WARNING, issues1.get(0).getType());
    assertEquals("Character repeater", issues1.get(0).getTitle());
    assertTrue(issues1.get(0).getDescription().contains("starString"));

    Parser p2 = any().plus().flatten();
    List<LinterIssue> issues2 = Linter.lint(p2, rules);
    assertEquals(1, issues2.size());
    assertSame(p2, issues2.get(0).getParser());
    assertEquals("Character repeater", issues2.get(0).getTitle());
  }

  @Test
  public void testCharacterRepeaterRuleNegative() {
    List<LinterRule> rules = List.of(new CharacterRepeaterRule());

    Parser p1 = of('a').plus().token();
    assertTrue(Linter.lint(p1, rules).isEmpty());

    Parser p2 = of("abc").star().flatten();
    assertTrue(Linter.lint(p2, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 2: DuplicateParserRule
  // --------------------------------------------------------------------------

  @Test
  public void testDuplicateParserRulePositive() {
    List<LinterRule> rules = List.of(new DuplicateParserRule());

    Parser p = digit().seq(digit());
    List<LinterIssue> issues = Linter.lint(p, rules);
    assertEquals(1, issues.size());
    assertSame(p.getChildren().get(0), issues.get(0).getParser());
    assertEquals(LinterType.INFO, issues.get(0).getType());
    assertEquals("Duplicate parser", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("2 instances"));
  }

  @Test
  public void testDuplicateParserRuleNegative() {
    List<LinterRule> rules = List.of(new DuplicateParserRule());

    Parser p = digit("first").seq(digit("second"));
    assertTrue(Linter.lint(p, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 3: LeftRecursionRule
  // --------------------------------------------------------------------------

  @Test
  public void testLeftRecursionRulePositive() {
    List<LinterRule> rules = List.of(new LeftRecursionRule());

    SettableParser s = SettableParser.undefined();
    s.set(s);
    List<LinterIssue> issues = Linter.lint(s, rules);
    assertEquals(1, issues.size());
    assertSame(s, issues.get(0).getParser());
    assertEquals(LinterType.ERROR, issues.get(0).getType());
    assertEquals("Left recursion", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("without consuming input"));
  }

  @Test
  public void testLeftRecursionRuleNegative() {
    List<LinterRule> rules = List.of(new LeftRecursionRule());

    Parser p = digit();
    assertTrue(Linter.lint(p, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 4: NestedChoiceRule
  // --------------------------------------------------------------------------

  @Test
  public void testNestedChoiceRulePositive() {
    List<LinterRule> rules = List.of(new NestedChoiceRule());

    Parser nested = of('2').or(of('3'));
    Parser p = new ChoiceParser(of('1'), nested, of('4'));
    List<LinterIssue> issues = Linter.lint(p, rules);
    assertEquals(1, issues.size());
    assertSame(p, issues.get(0).getParser());
    assertEquals(LinterType.INFO, issues.get(0).getType());
    assertEquals("Nested choice", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("index 1"));
  }

  @Test
  public void testNestedChoiceRuleNegative() {
    List<LinterRule> rules = List.of(new NestedChoiceRule());

    Parser p1 = of('1').or(of('2').or(of('3')).flatten(), of('4'));
    assertTrue(Linter.lint(p1, rules).isEmpty());

    Parser p2 = of('1').or(of('2'), of('3'), of('4'));
    assertTrue(Linter.lint(p2, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 5: NullableRepeaterRule
  // --------------------------------------------------------------------------

  @Test
  public void testNullableRepeaterRulePositive() {
    List<LinterRule> rules = List.of(new NullableRepeaterRule());

    Parser p1 = epsilon().star().optional();
    List<LinterIssue> issues1 = Linter.lint(p1, rules);
    assertEquals(1, issues1.size());
    assertSame(p1.getChildren().get(0), issues1.get(0).getParser());
    assertEquals(LinterType.ERROR, issues1.get(0).getType());
    assertEquals("Nullable repeater", issues1.get(0).getTitle());
    assertTrue(issues1.get(0).getDescription().contains("infinite loop"));

    // Separated repeating parser with both delegate and sep nullable
    Parser p2 = epsilon().starSeparated(epsilon());
    List<LinterIssue> issues2 = Linter.lint(p2, rules);
    assertEquals(1, issues2.size());
    assertEquals("Nullable repeater", issues2.get(0).getTitle());
  }

  @Test
  public void testNullableRepeaterRuleNegative() {
    List<LinterRule> rules = List.of(new NullableRepeaterRule());

    Parser p1 = digit().star().optional();
    assertTrue(Linter.lint(p1, rules).isEmpty());

    // Separated repeating with non-nullable delegate or non-nullable sep
    assertTrue(Linter.lint(epsilon().starSeparated(any()), rules).isEmpty());
    assertTrue(Linter.lint(any().starSeparated(epsilon()), rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 6: OverlappingChoiceRule
  // --------------------------------------------------------------------------

  @Test
  public void testOverlappingChoiceRulePositive() {
    List<LinterRule> rules = List.of(new OverlappingChoiceRule());

    Parser p = of('1').or(
        of('2').seq(of('a')),
        of('2').seq(of('b')),
        of('3')
    );
    List<LinterIssue> issues = Linter.lint(p, rules);
    assertEquals(1, issues.size());
    assertSame(p, issues.get(0).getParser());
    assertEquals(LinterType.INFO, issues.get(0).getType());
    assertEquals("Overlapping choice", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("overlapping first-sets"));
  }

  @Test
  public void testOverlappingChoiceRuleNegative() {
    List<LinterRule> rules = List.of(new OverlappingChoiceRule());

    Parser p = of('1').or(of('2'), of('3'));
    assertTrue(Linter.lint(p, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 7: RepeatedChoiceRule
  // --------------------------------------------------------------------------

  @Test
  public void testRepeatedChoiceRulePositive() {
    List<LinterRule> rules = List.of(new RepeatedChoiceRule());

    Parser p = new ChoiceParser(of('1'), of('2'), of('3'), of('2'), of('4'));
    List<LinterIssue> issues = Linter.lint(p, rules);
    assertEquals(1, issues.size());
    assertSame(p, issues.get(0).getParser());
    assertEquals(LinterType.WARNING, issues.get(0).getType());
    assertEquals("Repeated choice", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("index 1 and 3 are identical"));
  }

  @Test
  public void testRepeatedChoiceRuleNegative() {
    List<LinterRule> rules = List.of(new RepeatedChoiceRule());

    Parser p = of('1').or(of('2'), of('3'), of('4'));
    assertTrue(Linter.lint(p, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 8: UnnecessaryFlattenRule
  // --------------------------------------------------------------------------

  @Test
  public void testUnnecessaryFlattenRulePositive() {
    List<LinterRule> rules = List.of(new UnnecessaryFlattenRule());

    Parser p1 = any().flatten();
    List<LinterIssue> issues1 = Linter.lint(p1, rules);
    assertEquals(1, issues1.size());
    assertSame(p1, issues1.get(0).getParser());
    assertEquals(LinterType.WARNING, issues1.get(0).getType());
    assertEquals("Unnecessary flatten", issues1.get(0).getTitle());
    assertTrue(issues1.get(0).getDescription().contains("adds unnecessary overhead"));

    Parser p2 = of("hello").flatten();
    assertEquals(1, Linter.lint(p2, rules).size());

    Parser p3 = newline().flatten();
    assertEquals(1, Linter.lint(p3, rules).size());

    Parser p4 = digit().starString().flatten();
    assertEquals(1, Linter.lint(p4, rules).size());

    Parser p5 = any().flatten().flatten();
    assertEquals(2, Linter.lint(p5, rules).size());
  }

  @Test
  public void testUnnecessaryFlattenRuleNegative() {
    List<LinterRule> rules = List.of(new UnnecessaryFlattenRule());

    Parser p1 = any().optional().flatten();
    assertTrue(Linter.lint(p1, rules).isEmpty());

    Parser p2 = any().flatten("message");
    assertTrue(Linter.lint(p2, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 9: UnnecessaryResolvableRule
  // --------------------------------------------------------------------------

  @Test
  public void testUnnecessaryResolvableRulePositive() {
    List<LinterRule> rules = List.of(new UnnecessaryResolvableRule());

    SettableParser s = of('a').settable();
    List<LinterIssue> issues = Linter.lint(s, rules);
    assertEquals(1, issues.size());
    assertSame(s, issues.get(0).getParser());
    assertEquals(LinterType.WARNING, issues.get(0).getType());
    assertEquals("Unnecessary resolvable", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("resolve(parser)"));
  }

  @Test
  public void testUnnecessaryResolvableRuleNegative() {
    List<LinterRule> rules = List.of(new UnnecessaryResolvableRule());

    Parser p = of('a');
    assertTrue(Linter.lint(p, rules).isEmpty());

    Parser resolved = Analyzer.resolve(of('a').settable());
    assertTrue(Linter.lint(resolved, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 10: UnoptimizedFlattenRule
  // --------------------------------------------------------------------------

  @Test
  public void testUnoptimizedFlattenRulePositive() {
    List<LinterRule> rules = List.of(new UnoptimizedFlattenRule());

    Parser p = any().flatten();
    List<LinterIssue> issues = Linter.lint(p, rules);
    assertEquals(1, issues.size());
    assertSame(p, issues.get(0).getParser());
    assertEquals(LinterType.INFO, issues.get(0).getType());
    assertEquals("Unoptimized flatten", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("fast parsing mode"));
  }

  @Test
  public void testUnoptimizedFlattenRuleNegative() {
    List<LinterRule> rules = List.of(new UnoptimizedFlattenRule());

    Parser p = any().flatten("anything really");
    assertTrue(Linter.lint(p, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 11: UnreachableChoiceRule
  // --------------------------------------------------------------------------

  @Test
  public void testUnreachableChoiceRulePositive() {
    List<LinterRule> rules = List.of(new UnreachableChoiceRule());

    Parser p = new ChoiceParser(of('1'), of('2'), epsilon(), of('3'));
    List<LinterIssue> issues = Linter.lint(p, rules);
    assertEquals(1, issues.size());
    assertSame(p, issues.get(0).getParser());
    assertEquals(LinterType.WARNING, issues.get(0).getType());
    assertEquals("Unreachable choice", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("index 2 is nullable"));
  }

  @Test
  public void testUnreachableChoiceRuleNegative() {
    List<LinterRule> rules = List.of(new UnreachableChoiceRule());

    Parser p1 = of('1').or(of('2'), of('3'));
    assertTrue(Linter.lint(p1, rules).isEmpty());

    // Nullable choice as the last option is reachable and fine
    Parser p2 = of('1').or(of('2'), epsilon());
    assertTrue(Linter.lint(p2, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 12: UnresolvedSettableRule
  // --------------------------------------------------------------------------

  @Test
  public void testUnresolvedSettableRulePositive() {
    List<LinterRule> rules = List.of(new UnresolvedSettableRule());

    SettableParser s = SettableParser.undefined();
    List<LinterIssue> issues = Linter.lint(s, rules);
    assertEquals(1, issues.size());
    assertSame(s, issues.get(0).getParser());
    assertEquals(LinterType.ERROR, issues.get(0).getType());
    assertEquals("Unresolved settable", issues.get(0).getTitle());
    assertTrue(issues.get(0).getDescription().contains("undefined()"));
  }

  @Test
  public void testUnresolvedSettableRuleNegative() {
    List<LinterRule> rules = List.of(new UnresolvedSettableRule());

    Parser p1 = digit().settable();
    assertTrue(Linter.lint(p1, rules).isEmpty());

    SettableParser s2 = SettableParser.undefined();
    s2.set(digit());
    assertTrue(Linter.lint(s2, rules).isEmpty());
  }

  // --------------------------------------------------------------------------
  // Rule 13: UnusedResultRule
  // --------------------------------------------------------------------------

  @Test
  public void testUnusedResultRulePositive() {
    List<LinterRule> rules = List.of(new UnusedResultRule());

    Parser p1 = digit().map((Character c) -> Character.getNumericValue(c)).star().flatten();
    List<LinterIssue> issues1 = Linter.lint(p1, rules);
    assertEquals(1, issues1.size());
    assertSame(p1, issues1.get(0).getParser());
    assertEquals(LinterType.INFO, issues1.get(0).getType());
    assertEquals("Unused result", issues1.get(0).getTitle());
    assertTrue(issues1.get(0).getDescription().contains("discards the result"));

    Parser p2 = digit().token().flatten();
    assertEquals(1, Linter.lint(p2, rules).size());

    Parser p3 = digit().cast().flatten();
    assertEquals(1, Linter.lint(p3, rules).size());

    Parser p4 = digit().where(c -> true).flatten();
    assertEquals(1, Linter.lint(p4, rules).size());

    Parser p5 = digit().seq(digit()).pick(0).flatten();
    assertEquals(1, Linter.lint(p5, rules).size());

    Parser p6 = digit().seq(digit()).permute(1, 0).flatten();
    assertEquals(1, Linter.lint(p6, rules).size());
  }

  @Test
  public void testUnusedResultRuleNegative() {
    List<LinterRule> rules = List.of(new UnusedResultRule());

    Parser p1 = digit().star().flatten();
    assertTrue(Linter.lint(p1, rules).isEmpty());

    Parser p2 = digit().mapWithSideEffects(x -> x).flatten();
    assertTrue(Linter.lint(p2, rules).isEmpty());
  }


  // --------------------------------------------------------------------------
  // Engine Constants and Edge Cases
  // --------------------------------------------------------------------------

  @Test
  public void testEngineConstants() {
    assertEquals(13, Linter.ALL_RULES.size());
    Set<String> titles = new HashSet<>();
    for (LinterRule rule : Linter.ALL_RULES) {
      assertNotNull(rule.getTitle());
      assertNotNull(rule.getType());
      assertTrue(titles.add(rule.getTitle()));
      assertEquals(rule, rule);
      assertNotEquals(rule, null);
      assertNotEquals(rule, new Object());
      assertTrue(rule.toString().contains(rule.getTitle()));
    }
    assertEquals(Set.of(LinterType.INFO), Linter.DEFAULT_EXCLUDED_TYPES);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testAllRulesUnmodifiable() {
    Linter.ALL_RULES.add(new CharacterRepeaterRule());
  }

  @Test
  public void testLintVarargsAndCallbackMethods() {
    Parser p = of('a').star().flatten();
    List<LinterIssue> issues = Linter.lint(p, new CharacterRepeaterRule(), new DuplicateParserRule());
    assertEquals(1, issues.size());
    assertEquals("Character repeater", issues.get(0).getTitle());

    List<LinterIssue> collected = new ArrayList<>();
    issues = Linter.lint(p, collected::add);
    assertEquals(1, issues.size());
    assertEquals(issues, collected);

    Parser pSafe = of('a');
    collected.clear();
    issues = Linter.lint(pSafe, collected::add);
    assertTrue(issues.isEmpty());
    assertTrue(collected.isEmpty());
  }

  @Test
  public void testUnusedResultRuleWithConstantAndCastList() {
    List<LinterRule> rules = List.of(new UnusedResultRule());
    Parser p1 = digit().constant(42).flatten();
    assertEquals(1, Linter.lint(p1, rules).size());

    Parser p2 = digit().castList(String.class).flatten();
    assertEquals(1, Linter.lint(p2, rules).size());
  }

  @Test(expected = NullPointerException.class)
  public void testLintNullParser() {
    Linter.lint(null);
  }

  @Test(expected = NullPointerException.class)
  public void testQueryNullParser() {
    Linter.query(null);
  }

  @Test(expected = NullPointerException.class)
  public void testLinterRuleOfNullFunction() {
    LinterRule.of(LinterType.INFO, "Title", null);
  }

  @Test(expected = NullPointerException.class)
  public void testLinterRuleNullType() {
    new CharacterRepeaterRule() {
      {
        LinterRule.of(null, "Title", (r, a, p, cb) -> {});
      }
    };
  }

  @Test(expected = NullPointerException.class)
  public void testLinterRuleNullTitle() {
    LinterRule.of(LinterType.INFO, null, (r, a, p, cb) -> {});
  }

  @Test(expected = NullPointerException.class)
  public void testLinterIssueNullRule() {
    new LinterIssue(null, of('a'), "Description");
  }

  @Test(expected = NullPointerException.class)
  public void testLinterIssueNullParser() {
    new LinterIssue(new CharacterRepeaterRule(), null, "Description");
  }

  @Test(expected = NullPointerException.class)
  public void testLinterIssueNullDescription() {
    new LinterIssue(new CharacterRepeaterRule(), of('a'), null);
  }

  @Test(expected = NullPointerException.class)
  public void testFormatIterableNull() {
    LinterRule.formatIterable(null);
  }

  @Test(expected = NullPointerException.class)
  public void testIsParserIterableEqualNullFirst() {
    LinterRule.isParserIterableEqual(null, List.of());
  }

  @Test(expected = NullPointerException.class)
  public void testIsParserIterableEqualNullSecond() {
    LinterRule.isParserIterableEqual(List.of(), null);
  }
}
