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
}
