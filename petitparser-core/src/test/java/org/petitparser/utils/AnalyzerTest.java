package org.petitparser.utils;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.CharacterParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.combinators.SettableParser.undefined;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;
import static org.petitparser.parser.primitive.CharacterParser.lowerCase;
import static org.petitparser.parser.primitive.CharacterParser.upperCase;

/**
 * Tests for {@link Analyzer}.
 */
public class AnalyzerTest {

  @Test(expected = NullPointerException.class)
  public void testNullRoot() {
    Analyzer.of(null);
  }

  @Test
  public void testGetRootAndToString() {
    Parser parser = lowerCase();
    Analyzer analyzer = Analyzer.of(parser);
    assertSame(parser, analyzer.getRoot());
    assertEquals("Analyzer of " + parser, analyzer.toString());
  }

  @Test
  public void testSingleParserDiscovery() {
    Parser parser = lowerCase();
    Analyzer analyzer = Analyzer.of(parser);
    assertEquals(Collections.singletonList(parser), analyzer.parsers());
    assertEquals(Collections.singletonList(parser), analyzer.getParsers());
    assertEquals(Collections.singletonList(parser),
        analyzer.stream().collect(Collectors.toList()));
  }

  @Test
  public void testNestedParsersDiscovery() {
    Parser p3 = lowerCase();
    Parser p2 = p3.star();
    Parser p1 = p2.flatten();
    Analyzer analyzer = Analyzer.of(p1);
    assertEquals(Arrays.asList(p1, p2, p3), analyzer.parsers());
  }

  @Test
  public void testBranchedParsersDiscovery() {
    Parser p3 = lowerCase();
    Parser p2 = upperCase();
    Parser p1 = p2.seq(p3);
    Analyzer analyzer = Analyzer.of(p1);
    assertEquals(Arrays.asList(p1, p2, p3), analyzer.parsers());
  }

  @Test
  public void testDuplicateParsersDiscovery() {
    Parser p2 = upperCase();
    Parser p1 = p2.seq(p2);
    Analyzer analyzer = Analyzer.of(p1);
    assertEquals(Arrays.asList(p1, p2), analyzer.parsers());
  }

  @Test
  public void testKnotParserDiscovery() {
    SettableParser p1 = undefined();
    p1.set(p1);
    Analyzer analyzer = Analyzer.of(p1);
    assertEquals(Collections.singletonList(p1), analyzer.parsers());
  }

  @Test
  public void testLoopingParserDiscovery() {
    SettableParser p1 = undefined();
    SettableParser p2 = undefined();
    SettableParser p3 = undefined();
    p1.set(p2);
    p2.set(p3);
    p3.set(p1);
    Analyzer analyzer = Analyzer.of(p1);
    assertEquals(Arrays.asList(p1, p2, p3), analyzer.parsers());
  }

  @Test
  public void testAllChildren() {
    Parser p3 = lowerCase();
    Parser p2 = p3.star();
    Parser p1 = p2.flatten();
    Analyzer analyzer = Analyzer.of(p1);

    assertTrue(analyzer.allChildren(p3).isEmpty());
    assertEquals(Collections.singleton(p3), analyzer.allChildren(p2));
    assertEquals(Set.of(p2, p3), analyzer.allChildren(p1));
    assertSame(analyzer.allChildren(p1), analyzer.allChildren(p1));
  }

  @Test
  public void testAllChildrenCycles() {
    SettableParser p1 = undefined();
    SettableParser p2 = undefined();
    p1.set(p2);
    p2.set(p1);
    Analyzer analyzer = Analyzer.of(p1);

    assertEquals(Set.of(p1, p2), analyzer.allChildren(p1));
    assertEquals(Set.of(p1, p2), analyzer.allChildren(p2));
  }

  @Test
  public void testFindPathDirectAndTransitive() {
    Parser p3 = lowerCase();
    Parser p2 = p3.star();
    Parser p1 = p2.flatten();
    Analyzer analyzer = Analyzer.of(p1);

    assertEquals(Collections.singletonList(p1), analyzer.findPath(p1, p1));
    assertEquals(Arrays.asList(p1, p2), analyzer.findPath(p1, p2));
    assertEquals(Arrays.asList(p1, p2, p3), analyzer.findPath(p1, p3));
    assertEquals(Arrays.asList(p1, p2, p3), analyzer.findPathTo(p3));

    Parser unrelated = digit();
    assertTrue(analyzer.findPath(p1, unrelated).isEmpty());
    assertTrue(analyzer.findPathTo(unrelated).isEmpty());
  }

  @Test
  public void testFindPathShortest() {
    Parser target = digit();
    Parser longBranch = lowerCase().seq(target);
    Parser shortBranch = target;
    Parser root = longBranch.or(shortBranch);
    Analyzer analyzer = Analyzer.of(root);

    List<Parser> path = analyzer.findPath(root, target);
    assertEquals(Arrays.asList(root, target), path);
  }

  @Test
  public void testFindPathWithPredicate() {
    Parser p3 = lowerCase();
    Parser p2 = p3.star();
    Parser p1 = p2.flatten();
    Analyzer analyzer = Analyzer.of(p1);

    List<Parser> path = analyzer.findPathTo(p -> p instanceof CharacterParser);
    assertEquals(Arrays.asList(p1, p2, p3), path);

    List<Parser> notFound = analyzer.findPath(p1, p -> false);
    assertTrue(notFound.isEmpty());
  }

  @Test
  public void testFindAllPathsDiamond() {
    Parser target = digit();
    Parser b = target.star();
    Parser c = target.plus();
    Parser a = b.seq(c);
    Analyzer analyzer = Analyzer.of(a);

    List<List<Parser>> paths = analyzer.findAllPathsTo(target);
    assertEquals(2, paths.size());
    assertTrue(paths.contains(Arrays.asList(a, b, target)));
    assertTrue(paths.contains(Arrays.asList(a, c, target)));

    assertEquals(paths, analyzer.findAllPaths(a, target));
    assertEquals(paths, analyzer.findAllPathsTo(p -> p == target));
  }

  @Test
  public void testFindAllPathsCycle() {
    SettableParser p1 = undefined();
    Parser target = digit();
    p1.set(target.or(p1));
    Analyzer analyzer = Analyzer.of(p1);

    List<List<Parser>> paths = analyzer.findAllPathsTo(target);
    assertEquals(1, paths.size());
    assertEquals(Arrays.asList(p1, p1.getChildren().get(0), target), paths.get(0));
  }

  @Test
  public void testFindCycle() {
    Parser p1 = letter().star();
    Analyzer analyzer = Analyzer.of(p1);
    assertTrue(analyzer.findCycle(p1).isEmpty());

    SettableParser knot = undefined();
    knot.set(knot);
    Analyzer knotAnalyzer = Analyzer.of(knot);
    assertEquals(Arrays.asList(knot, knot), knotAnalyzer.findCycle(knot));

    SettableParser c1 = undefined();
    SettableParser c2 = undefined();
    c1.set(c2);
    c2.set(c1);
    Analyzer cycleAnalyzer = Analyzer.of(c1);
    assertEquals(Arrays.asList(c1, c2, c1), cycleAnalyzer.findCycle(c1));
    assertEquals(Arrays.asList(c2, c1, c2), cycleAnalyzer.findCycle(c2));
  }

  @Test
  public void testCycleSetAndIsCyclic() {
    Parser acyclic = letter().seq(digit());
    Analyzer analyzer = Analyzer.of(acyclic);
    assertFalse(analyzer.isCyclic());
    assertFalse(analyzer.isCyclic(acyclic));
    assertTrue(analyzer.cycleSet(acyclic).isEmpty());
    assertTrue(analyzer.cycles().isEmpty());

    SettableParser knot = undefined();
    knot.set(knot);
    Analyzer knotAnalyzer = Analyzer.of(knot);
    assertTrue(knotAnalyzer.isCyclic());
    assertTrue(knotAnalyzer.isCyclic(knot));
    assertEquals(Set.of(knot), knotAnalyzer.cycleSet(knot));
    assertEquals(Set.of(knot), knotAnalyzer.cycles());

    SettableParser c1 = undefined();
    SettableParser c2 = undefined();
    c1.set(c2);
    c2.set(c1);
    Analyzer cycleAnalyzer = Analyzer.of(c1);
    assertTrue(cycleAnalyzer.isCyclic(c1));
    assertTrue(cycleAnalyzer.isCyclic(c2));
    assertEquals(Set.of(c1, c2), cycleAnalyzer.cycleSet(c1));
    assertEquals(Set.of(c1, c2), cycleAnalyzer.cycleSet(c2));

    Parser root = letter().seq(c1);
    Analyzer rootAnalyzer = Analyzer.of(root);
    assertTrue(rootAnalyzer.isCyclic());
    assertFalse(rootAnalyzer.isCyclic(root));
    assertTrue(rootAnalyzer.cycleSet(root).isEmpty());
    assertTrue(rootAnalyzer.isCyclic(c1));
    assertEquals(Set.of(c1, c2), rootAnalyzer.cycles());
  }

  @Test
  public void testIsNullableTerminalsAndCombinators() {
    Parser letter = letter();
    Parser eps = org.petitparser.parser.primitive.EpsilonParser.INSTANCE;
    Parser pos = org.petitparser.parser.primitive.PositionParser.INSTANCE;
    Parser fail = org.petitparser.parser.primitive.FailureParser.withMessage("err");

    Parser root = letter.seq(eps).seq(pos).seq(fail);
    Analyzer analyzer = Analyzer.of(root);

    assertFalse(analyzer.isNullable(letter));
    assertTrue(analyzer.isNullable(eps));
    assertTrue(analyzer.isNullable(pos));
    assertFalse(analyzer.isNullable(fail));

    assertTrue(analyzer.isNullable(letter.optional()));
    assertTrue(analyzer.isNullable(letter.star()));
    assertFalse(analyzer.isNullable(letter.plus()));
    assertTrue(analyzer.isNullable(letter.repeat(0, 3)));
    assertFalse(analyzer.isNullable(letter.repeat(1, 3)));

    assertTrue(analyzer.isNullable(letter.repeatSeparated(digit(), 0, 3)));
    assertFalse(analyzer.isNullable(letter.repeatSeparated(digit(), 1, 3)));
    assertTrue(analyzer.isNullable(letter.starSeparated(digit())));
    assertFalse(analyzer.isNullable(letter.plusSeparated(digit())));

    // Sequences
    assertTrue(analyzer.isNullable(eps.seq(letter.star())));
    assertFalse(analyzer.isNullable(eps.seq(letter)));

    // Choices
    assertTrue(analyzer.isNullable(letter.or(eps)));
    assertFalse(analyzer.isNullable(letter.or(digit())));

    // Trimming
    assertFalse(analyzer.isNullable(letter.trim()));
    assertTrue(analyzer.isNullable(letter.optional().trim()));

    // Not & And
    assertTrue(analyzer.isNullable(letter.not()));
    assertFalse(analyzer.isNullable(eps.not()));
    assertFalse(analyzer.isNullable(letter.and()));
    assertTrue(analyzer.isNullable(eps.and()));
  }

  @Test
  public void testIsNullableRecursive() {
    SettableParser p = undefined();
    p.set(p.or(org.petitparser.parser.primitive.EpsilonParser.INSTANCE));
    Analyzer analyzer = Analyzer.of(p);
    assertTrue(analyzer.isNullable(p));

    SettableParser nonNull = undefined();
    nonNull.set(nonNull.seq(digit()).or(letter()));
    Analyzer nonNullAnalyzer = Analyzer.of(nonNull);
    assertFalse(nonNullAnalyzer.isNullable(nonNull));
  }

  @Test
  public void testFirstSet() {
    Parser a = CharacterParser.of('a');
    Parser b = CharacterParser.of('b');
    Parser c = CharacterParser.of('c');

    Analyzer aAnalyzer = Analyzer.of(a);
    assertEquals(Set.of(a), aAnalyzer.firstSet(a));

    Parser seq = a.seq(b);
    Analyzer seqAnalyzer = Analyzer.of(seq);
    assertEquals(Set.of(a), seqAnalyzer.firstSet(seq));

    Parser nullableSeq = a.optional().seq(b);
    Analyzer nullableSeqAnalyzer = Analyzer.of(nullableSeq);
    assertEquals(Set.of(a, b), nullableSeqAnalyzer.firstSet(nullableSeq));

    Parser allNullableSeq = a.optional().seq(b.optional()).seq(c);
    Analyzer allNullableSeqAnalyzer = Analyzer.of(allNullableSeq);
    assertEquals(Set.of(a, b, c), allNullableSeqAnalyzer.firstSet(allNullableSeq));

    Parser choice = a.or(b);
    Analyzer choiceAnalyzer = Analyzer.of(choice);
    assertEquals(Set.of(a, b), choiceAnalyzer.firstSet(choice));

    Parser sep = a.separatedBy(b);
    Analyzer sepAnalyzer = Analyzer.of(sep);
    assertEquals(Set.of(a), sepAnalyzer.firstSet(sep));
  }

  @Test
  public void testFirstSetLeftRecursive() {
    SettableParser expr = undefined();
    Parser plus = CharacterParser.of('+');
    Parser num = digit();
    expr.set(expr.seq(plus).seq(num).or(num));
    Analyzer analyzer = Analyzer.of(expr);

    assertEquals(Set.of(num), analyzer.firstSet(expr));
  }

  @Test
  public void testFollowSet() {
    Parser a = CharacterParser.of('a');
    Parser b = CharacterParser.of('b');
    Parser c = CharacterParser.of('c');

    Parser seq = a.seq(b);
    Analyzer seqAnalyzer = Analyzer.of(seq);
    assertEquals(Set.of(b), seqAnalyzer.followSet(a));
    assertTrue(seqAnalyzer.followSet(b).isEmpty());

    Parser nullableIntermediate = a.seq(b.optional()).seq(c);
    Analyzer nullableAnalyzer = Analyzer.of(nullableIntermediate);
    assertEquals(Set.of(b, c), nullableAnalyzer.followSet(a));
    assertEquals(Set.of(c), nullableAnalyzer.followSet(b));

    Parser loop = a.seq(b).star();
    Analyzer loopAnalyzer = Analyzer.of(loop);
    assertEquals(Set.of(a), loopAnalyzer.followSet(b));

    Parser sepList = a.separatedBy(b);
    Analyzer sepAnalyzer = Analyzer.of(sepList);
    assertEquals(Set.of(b), sepAnalyzer.followSet(a));
    assertEquals(Set.of(a), sepAnalyzer.followSet(b));
  }

  @Test
  public void testResolveNonResolvable() {
    Parser parser = letter().star();
    assertSame(parser, Analyzer.resolve(parser));
    assertSame(parser, Analyzer.of(parser).resolve());
  }

  @Test
  public void testResolveSingleSettable() {
    CharacterParser inner = CharacterParser.of('a');
    SettableParser settable = SettableParser.with(inner);
    Parser resolved = Analyzer.resolve(settable);
    assertSame(inner, resolved);
  }

  @Test
  public void testResolveChainedSettable() {
    CharacterParser inner = CharacterParser.of('x');
    SettableParser s1 = undefined();
    SettableParser s2 = undefined();
    s1.set(s2);
    s2.set(inner);
    Parser resolved = Analyzer.resolve(s1);
    assertSame(inner, resolved);
  }

  @Test(expected = IllegalStateException.class)
  public void testResolveCyclicSettableError() {
    SettableParser s1 = undefined();
    SettableParser s2 = undefined();
    s1.set(s2);
    s2.set(s1);
    Analyzer.resolve(s1);
  }

  @Test
  public void testResolveInTree() {
    CharacterParser a = CharacterParser.of('a');
    CharacterParser b = CharacterParser.of('b');
    SettableParser s = undefined();
    Parser seq = a.seq(s);
    s.set(b);

    Parser resolved = Analyzer.resolve(seq);
    assertSame(seq, resolved);
    assertEquals(Arrays.asList(a, b), resolved.getChildren());
    org.petitparser.Assertions.assertSuccess(resolved, "ab", Arrays.asList('a', 'b'));
  }

  @Test
  public void testResolveRecursiveGrammar() {
    SettableParser expr = undefined();
    Parser prim = digit().plus().flatten();
    Parser group = CharacterParser.of('(').seq(expr).seq(CharacterParser.of(')')).pick(1);
    expr.set(prim.or(group));

    Parser resolved = Analyzer.of(expr).resolve();
    assertFalse(resolved instanceof org.petitparser.parser.combinators.ResolvableParser);

    List<Parser> all = Analyzer.of(resolved).parsers();
    for (Parser p : all) {
      assertFalse(p instanceof org.petitparser.parser.combinators.ResolvableParser);
    }

    org.petitparser.Assertions.assertSuccess(resolved, "42", "42");
    org.petitparser.Assertions.assertSuccess(resolved, "(42)", "42");
    org.petitparser.Assertions.assertSuccess(resolved, "((123))", "123");
    org.petitparser.Assertions.assertFailure(resolved, "(42", 3, "')' expected");
  }
}

