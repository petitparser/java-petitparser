package org.petitparser.utils;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.utils.linter.CharacterRepeaterRule;
import org.petitparser.utils.linter.DuplicateParserRule;
import org.petitparser.utils.linter.LinterIssue;
import org.petitparser.utils.linter.LinterRule;
import org.petitparser.utils.linter.LinterType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Parallel unit tests for facade {@link Linter}.
 */
public class LinterTest {

  @Test
  public void testLintDefault() {
    Parser parser = of('a');
    List<LinterIssue> issues = Linter.lint(parser);
    assertTrue(issues.isEmpty());
  }

  @Test
  public void testLintWithRulesList() {
    Parser parser = digit().seq(digit());
    List<LinterIssue> issues = Linter.lint(parser, List.of(new DuplicateParserRule()));
    assertEquals(1, issues.size());
    assertEquals("Duplicate parser", issues.get(0).getTitle());
  }

  @Test
  public void testLintWithRulesVarargs() {
    Parser parser = of('a').star().flatten();
    List<LinterIssue> issues = Linter.lint(parser, new CharacterRepeaterRule(), new DuplicateParserRule());
    assertEquals(1, issues.size());
    assertEquals("Character repeater", issues.get(0).getTitle());
  }

  @Test
  public void testLintWithCallback() {
    Parser parser = SettableParser.undefined();
    List<LinterIssue> callbackIssues = new ArrayList<>();
    List<LinterIssue> issues = Linter.lint(parser, callbackIssues::add);
    assertFalse(issues.isEmpty());
    assertEquals(issues, callbackIssues);
    assertTrue(issues.stream().anyMatch(i -> i.getTitle().equals("Unresolved settable")));
    assertTrue(issues.stream().anyMatch(i -> i.getTitle().equals("Unnecessary resolvable")));
  }

  @Test
  public void testLintWithCallbackAndRulesList() {
    Parser parser = of('a').star().flatten();
    AtomicInteger count = new AtomicInteger();
    List<LinterIssue> issues = Linter.lint(
        parser,
        issue -> count.incrementAndGet(),
        List.of(new CharacterRepeaterRule())
    );
    assertEquals(1, issues.size());
    assertEquals(1, count.get());
  }

  @Test
  public void testLintFullyConfigured() {
    Parser parser = of('a').or(of('b').or(of('c'))); // Nested choice is INFO
    List<LinterIssue> issues = Linter.lint(
        parser,
        null,
        null,
        Collections.emptySet(),
        Collections.emptySet() // include INFO
    );
    assertFalse(issues.isEmpty());
    assertEquals("Nested choice", issues.get(0).getTitle());
  }

  @Test
  public void testQueryFacade() {
    Parser parser = of('a').star().flatten();
    assertNotNull(Linter.query(parser));
    List<LinterIssue> issues = Linter.query(parser)
        .rules(new CharacterRepeaterRule())
        .lint();
    assertEquals(1, issues.size());
    assertEquals("Character repeater", issues.get(0).getTitle());
  }
}
