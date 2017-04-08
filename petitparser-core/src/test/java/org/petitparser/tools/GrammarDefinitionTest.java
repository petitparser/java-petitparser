package org.petitparser.tools;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.EpsilonParser;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.CharacterParser.pattern;
import static org.petitparser.parser.primitive.CharacterParser.word;

/**
 * Tests {@link GrammarDefinition}.
 */
public class GrammarDefinitionTest {

  class ListGrammarDefinition extends GrammarDefinition {
    ListGrammarDefinition() {
      def("start", ref("list").end());
      def("list", ref("element").seq(of(',').flatten()).seq(ref("list")).or(ref("element")));
      def("element", digit().plus().flatten());
    }
  }

  class ListParserDefinition extends ListGrammarDefinition {
    ListParserDefinition() {
      action("element", (String value) -> Integer.parseInt(value));
    }
  }

  class BuggedGrammarDefinition extends GrammarDefinition {
    BuggedGrammarDefinition() {
      def("start", new EpsilonParser());

      def("directRecursion1", ref("directRecursion1"));

      def("indirectRecursion1", ref("indirectRecursion2"));
      def("indirectRecursion2", ref("indirectRecursion3"));
      def("indirectRecursion3", ref("indirectRecursion1"));

      def("delegation1", ref("delegation2"));
      def("delegation2", ref("delegation3"));
      def("delegation3", new EpsilonParser());

      def("unknownReference", ref("unknown"));
    }
  }

  class LambdaGrammarDefinition extends GrammarDefinition {
    LambdaGrammarDefinition() {
      def("start", ref("expression").end());
      def("expression", ref("variable")
          .or(ref("abstraction"))
          .or(ref("application")));
      def("variable", letter().seq(word().star()).flatten().trim());
      def("abstraction", of('\\').trim()
          .seq(ref("variable"))
          .seq(of('.').trim())
          .seq(ref("expression")));
      def("application", of('(').trim()
          .seq(ref("expression"))
          .seq(ref("expression"))
          .seq(of(')').trim()));
    }
  }

  class ExpressionGrammarDefinition extends GrammarDefinition {
    ExpressionGrammarDefinition() {
      def("start", ref("terms").end());

      def("terms", ref("addition").or(ref("factors")));
      def("addition", ref("factors").separatedBy(pattern("+-").flatten().trim()));

      def("factors", ref("multiplication").or(ref("power")));
      def("multiplication", ref("power").separatedBy(pattern("*/").flatten().trim()));

      def("power", ref("primary").separatedBy(of('^').flatten().trim()));
      def("primary", ref("number").or(ref("parentheses")));

      def("number", of('-').flatten().trim().optional()
          .seq(digit().plus())
          .seq(of('.')
              .seq(digit().plus())
              .optional()));
      def("parentheses", of('(').flatten().trim()
          .seq(ref("terms"))
          .seq(of(')').flatten().trim()));
    }
  }

  private final GrammarDefinition grammarDefinition = new ListGrammarDefinition();
  private final GrammarDefinition parserDefinition = new ListParserDefinition();
  private final GrammarDefinition buggedDefinition = new BuggedGrammarDefinition();
  private final GrammarDefinition lambdaDefinition = new LambdaGrammarDefinition();
  private final GrammarDefinition expressionDefinition = new ExpressionGrammarDefinition();

  @Test
  public void testGrammar() {
    Parser parser = grammarDefinition.build();
    assertEquals(Arrays.asList("1", ",", "2"), parser.parse("1,2").get());
    assertEquals(Arrays.asList("1", ",", Arrays.asList("2", ",", "3")),
        parser.parse("1,2,3").get());
  }

  @Test
  public void testParser() {
    Parser parser = parserDefinition.build();
    assertEquals(Arrays.asList(1, ",", 2), parser.parse("1,2").get());
    assertEquals(Arrays.asList(1, ",", Arrays.asList(2, ",", 3)), parser.parse("1,2,3").get());
  }

  @Test(expected = IllegalStateException.class)
  public void testDirectRecursion1() {
    buggedDefinition.build("directRecursion1");
  }

  @Test(expected = IllegalStateException.class)
  public void testIndirectRecursion1() {
    buggedDefinition.build("indirectRecursion1");
  }

  @Test(expected = IllegalStateException.class)
  public void testIndirectRecursion2() {
    buggedDefinition.build("indirectRecursion2");
  }

  @Test(expected = IllegalStateException.class)
  public void testIndirectRecursion3() {
    buggedDefinition.build("indirectRecursion3");
  }

  @Test(expected = IllegalStateException.class)
  public void testUnknownReferenceOutside() {
    buggedDefinition.build("unknown");
  }

  @Test(expected = IllegalStateException.class)
  public void testUnknownReferenceInside() {
    buggedDefinition.build("unknownReference");
  }

  @Test(expected = IllegalStateException.class)
  public void testDuplicateDefinition() {
    buggedDefinition.def("start", new EpsilonParser());
  }

  @Test(expected = IllegalStateException.class)
  public void testUnknownRedefinition() {
    buggedDefinition.redef("unknown", new EpsilonParser());
  }

  @Test(expected = IllegalStateException.class)
  public void testUnknownRedefinitionFunction() {
    buggedDefinition.redef("unknown", (start) -> new EpsilonParser());
  }

  @Test(expected = IllegalStateException.class)
  public void testUnknownAction() {
    buggedDefinition.action("unknown", (object) -> null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testReferenceParse() {
    buggedDefinition.ref("start").parse("abc");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testReferenceCopy() {
    buggedDefinition.ref("start").copy();
  }

  @Test
  public void testReferenceEquals(){
    Parser reference = buggedDefinition.ref("start");
    assertTrue(reference.equals(reference));
    assertFalse(reference.equals(null));
    assertFalse(reference.equals("start"));
  }

  @Test
  public void testDelegation1() {
    assertTrue(buggedDefinition.build("delegation1") instanceof EpsilonParser);
  }

  @Test
  public void testDelegation2() {
    assertTrue(buggedDefinition.build("delegation2") instanceof EpsilonParser);
  }

  @Test
  public void testDelegation3() {
    assertTrue(buggedDefinition.build("delegation3") instanceof EpsilonParser);
  }

  @Test
  public void testLambdaGrammar() {
    Parser parser = new GrammarParser(lambdaDefinition);
    assertTrue(parser.accept("x"));
    assertTrue(parser.accept("xy"));
    assertTrue(parser.accept("x12"));
    assertTrue(parser.accept("\\x.y"));
    assertTrue(parser.accept("\\x.\\y.z"));
    assertTrue(parser.accept("(x x)"));
    assertTrue(parser.accept("(x y)"));
    assertTrue(parser.accept("(x (y z))"));
    assertTrue(parser.accept("((x y) z)"));
  }

  @Test
  public void testExpressionGrammar() {
    Parser parser = new GrammarParser(expressionDefinition, "start");
    assertTrue(parser.accept("1"));
    assertTrue(parser.accept("12"));
    assertTrue(parser.accept("1.23"));
    assertTrue(parser.accept("-12.3"));
    assertTrue(parser.accept("1 + 2"));
    assertTrue(parser.accept("1 + 2 + 3"));
    assertTrue(parser.accept("1 - 2"));
    assertTrue(parser.accept("1 - 2 - 3"));
    assertTrue(parser.accept("1 * 2"));
    assertTrue(parser.accept("1 * 2 * 3"));
    assertTrue(parser.accept("1 / 2"));
    assertTrue(parser.accept("1 / 2 / 3"));
    assertTrue(parser.accept("1 ^ 2"));
    assertTrue(parser.accept("1 ^ 2 ^ 3"));
    assertTrue(parser.accept("1 + (2 * 3)"));
    assertTrue(parser.accept("(1 + 2) * 3"));
  }
}