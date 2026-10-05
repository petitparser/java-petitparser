package org.petitparser.tools;

import org.junit.Test;
import org.petitparser.parser.Parser;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests for {@link GrammarDefinition} using {@link Production}.
 */
public class TypedGrammarDefinitionTest {

  static final Production<Integer> START = GrammarDefinition.production("start");
  static final Production<Integer> ADD = GrammarDefinition.production("add");
  static final Production<Integer> NUMBER = GrammarDefinition.production("number");

  static class TypedArithmeticGrammarDefinition extends GrammarDefinition {
    TypedArithmeticGrammarDefinition() {
      def(START, ref(ADD).end());
      def(ADD, Parser.seq(ref(NUMBER), of('+').trim(), ref(NUMBER))
          .map((a, op, b) -> (Integer) a + (Integer) b));
      def(NUMBER, digit().plus().flatten().trim().map((Function<String, Integer>) Integer::parseInt));
    }
  }

  static class TypedArithmeticParserDefinition extends TypedArithmeticGrammarDefinition {
    TypedArithmeticParserDefinition() {
      action(NUMBER, (Function<Integer, Integer>) n -> n * 10);
    }
  }

  @Test
  public void testProductionProperties() {
    Production<String> prod1 = Production.of("foo");
    Production<String> prod2 = GrammarDefinition.production("foo");
    Production<Integer> prod3 = Production.of("bar");

    assertEquals("foo", prod1.getName());
    assertEquals(prod1, prod1);
    assertEquals(prod1, prod2);
    assertEquals(prod1.hashCode(), prod2.hashCode());
    assertNotEquals(prod1, prod3);
    assertNotEquals(prod1, null);
    assertNotEquals(prod1, "foo");
    assertEquals("Production[foo]", prod1.toString());

    try {
      Production.of(null);
      org.junit.Assert.fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }
  }

  @Test
  public void testTypedGrammarBuild() {
    TypedArithmeticGrammarDefinition definition = new TypedArithmeticGrammarDefinition();
    Parser parser = definition.build(START);

    assertSuccess(parser, "12 + 34", 46);
  }

  @Test
  public void testTypedGrammarParser() {
    TypedArithmeticGrammarDefinition definition = new TypedArithmeticGrammarDefinition();
    GrammarParser parser = new GrammarParser(definition, START);

    assertSuccess(parser, "5 + 7", 12);
  }

  @Test
  public void testTypedAction() {
    TypedArithmeticParserDefinition definition = new TypedArithmeticParserDefinition();
    Parser parser = definition.build(START);

    // Number multiplied by 10 in action: 10 + 20 = 30
    assertSuccess(parser, "1 + 2", 30);
  }

  @Test
  public void testRedef() {
    Production<String> ITEM = Production.of("item");
    GrammarDefinition def = new GrammarDefinition() {
      {
        def(ITEM, of('a'));
        redef(ITEM, of('b'));
      }
    };
    Parser parser = def.build(ITEM);
    assertSuccess(parser, "b", 'b');
  }

  @Test
  public void testRedefWithFunction() {
    Production<String> ITEM = Production.of("item");
    GrammarDefinition def = new GrammarDefinition() {
      {
        def(ITEM, of('a'));
        redef(ITEM, (Parser p) -> p.seq(of('b')));
      }
    };
    Parser parser = def.build(ITEM);
    assertSuccess(parser, "ab", java.util.Arrays.asList('a', 'b'));
  }
}
