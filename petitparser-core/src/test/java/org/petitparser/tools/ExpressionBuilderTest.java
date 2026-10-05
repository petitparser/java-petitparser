package org.petitparser.tools;

import org.junit.Before;
import org.junit.Test;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.EpsilonParser;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.petitparser.utils.functions.Function3;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.noneOf;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.StringParser.of;

/**
 * Tests {@link ExpressionBuilder}.
 */
public class ExpressionBuilderTest {

  Parser parser;

  @Before
  @SuppressWarnings("rawtypes")
  public void setUpParser() {
    ExpressionBuilder builder = new ExpressionBuilder();
    builder.group()
        .primitive(digit().plus().seq(of('.')
                .seq(digit().plus()).optional())
            .flatten()
            .trim())
        .wrapper(of('(').trim(), of(')').trim());
    builder.group()
        .prefix(of('-').trim());
    builder.group()
        .postfix(of("++").trim())
        .postfix(of("--").trim());
    builder.group()
        .right(of('^').trim());
    builder.group()
        .left(of('*').trim())
        .left(of('/').trim());
    builder.group()
        .left(of('+').trim())
        .left(of('-').trim());
    parser = builder.build().end();
  }

  private void assertParse(String input, Object expected) {
    Object actual = parser.parse(input).get();
    assertEquals(expected, actual);
  }

  Parser evaluator;

  @Before
  public void setUpEvaluator() {
    ExpressionBuilder<Double> builder = new ExpressionBuilder<>();
    builder.group()
        .primitive(digit().plus().seq(of('.')
                .seq(digit().plus()).optional())
            .flatten()
            .trim()
            .map(Double::parseDouble))
        .wrapper(
            of('(').trim(),
            of(')').trim(),
            (List<Double> values) -> values.get(1));
    builder.group()
        .prefix(of('-').trim(), (List<Double> values) -> -values.get(1));
    builder.group()
        .postfix(of("++").trim(), (List<Double> values) -> values.get(0) + 1)
        .postfix(of("--").trim(), (List<Double> values) -> values.get(0) - 1);
    builder.group()
        .right(of('^').trim(),
            (List<Double> values) -> Math.pow(values.get(0), values.get(2)));
    builder.group()
        .left(of('*').trim(),
            (List<Double> values) -> values.get(0) * values.get(2))
        .left(of('/').trim(),
            (List<Double> values) -> values.get(0) / values.get(2));
    builder.group()
        .left(of('+').trim(),
            (List<Double> values) -> values.get(0) + values.get(2))
        .left(of('-').trim(),
            (List<Double> values) -> values.get(0) - values.get(2));
    evaluator = builder.build().end();
  }

  private void assertEvaluation(String input, double expected) {
    double actual = evaluator.parse(input).get();
    assertEquals(expected, actual, 1e-5);
  }

  @Test
  public void testParseNumber() {
    assertParse("0", "0");
    assertParse("1.2", "1.2");
    assertParse("34.78", "34.78");
  }

  @Test
  public void testEvaluateNumber() {
    assertEvaluation("0", 0);
    assertEvaluation("0.0", 0);
    assertEvaluation("1", 1);
    assertEvaluation("1.2", 1.2);
    assertEvaluation("34", 34);
    assertEvaluation("34.7", 34.7);
    assertEvaluation("56.78", 56.78);
  }

  @Test
  public void testParseNegativeNumber() {
    assertParse("-1", asList('-', "1"));
    assertParse("-1.2", asList('-', "1.2"));
  }

  @Test
  public void testEvaluateNegativeNumber() {
    assertEvaluation("-1", -1);
    assertEvaluation("-1.2", -1.2);
  }

  @Test
  public void testParseAdd() {
    assertParse("1 + 2", asList("1", '+', "2"));
    assertParse("1 + 2 + 3", asList(asList("1", '+', "2"), '+', "3"));
  }

  @Test
  public void testEvaluateAdd() {
    assertEvaluation("1 + 2", 3);
    assertEvaluation("2 + 1", 3);
    assertEvaluation("1 + 2.3", 3.3);
    assertEvaluation("2.3 + 1", 3.3);
    assertEvaluation("1 + -2", -1);
    assertEvaluation("-2 + 1", -1);
  }

  @Test
  public void testEvaluateAddMany() {
    assertEvaluation("1", 1);
    assertEvaluation("1 + 2", 3);
    assertEvaluation("1 + 2 + 3", 6);
    assertEvaluation("1 + 2 + 3 + 4", 10);
    assertEvaluation("1 + 2 + 3 + 4 + 5", 15);
  }

  @Test
  public void testParseSub() {
    assertParse("1 - 2", asList("1", '-', "2"));
    assertParse("1 - 2 - 3", asList(asList("1", '-', "2"), '-', "3"));
  }

  @Test
  public void testEvaluateSub() {
    assertEvaluation("1 - 2", -1);
    assertEvaluation("1.2 - 1.2", 0);
    assertEvaluation("1 - -2", 3);
    assertEvaluation("-1 - -2", 1);
  }

  @Test
  public void testEvaluateSubMany() {
    assertEvaluation("1", 1);
    assertEvaluation("1 - 2", -1);
    assertEvaluation("1 - 2 - 3", -4);
    assertEvaluation("1 - 2 - 3 - 4", -8);
    assertEvaluation("1 - 2 - 3 - 4 - 5", -13);
  }

  @Test
  public void testParseMul() {
    assertParse("1 * 2", asList("1", '*', "2"));
    assertParse("1 * 2 * 3", asList(asList("1", '*', "2"), '*', "3"));
  }

  @Test
  public void testEvaluateMul() {
    assertEvaluation("2 * 3", 6);
    assertEvaluation("2 * -4", -8);
  }

  @Test
  public void testEvaluateMulMany() {
    assertEvaluation("1 * 2", 2);
    assertEvaluation("1 * 2 * 3", 6);
    assertEvaluation("1 * 2 * 3 * 4", 24);
    assertEvaluation("1 * 2 * 3 * 4 * 5", 120);
  }

  @Test
  public void testParseDiv() {
    assertParse("1 / 2", asList("1", '/', "2"));
    assertParse("1 / 2 / 3", asList(asList("1", '/', "2"), '/', "3"));
  }

  @Test
  public void testEvaluateDiv() {
    assertEvaluation("12 / 3", 4);
    assertEvaluation("-16 / -4", 4);
  }

  @Test
  public void testEvaluateDivMany() {
    assertEvaluation("100 / 2", 50);
    assertEvaluation("100 / 2 / 2", 25);
    assertEvaluation("100 / 2 / 2 / 5", 5);
    assertEvaluation("100 / 2 / 2 / 5 / 5", 1);
  }

  @Test
  public void testParsePow() {
    assertParse("1 ^ 2", asList("1", '^', "2"));
    assertParse("1 ^ 2 ^ 3", asList("1", '^', asList("2", '^', "3")));
  }

  @Test
  public void testEvaluatePow() {
    assertEvaluation("2 ^ 3", 8);
    assertEvaluation("-2 ^ 3", -8);
    assertEvaluation("-2 ^ -3", -0.125);
  }

  @Test
  public void testEvaluatePowMany() {
    assertEvaluation("4 ^ 3", 64);
    assertEvaluation("4 ^ 3 ^ 2", 262144);
    assertEvaluation("4 ^ 3 ^ 2 ^ 1", 262144);
    assertEvaluation("4 ^ 3 ^ 2 ^ 1 ^ 0", 262144);
  }

  @Test
  public void testParseParenthesis() {
    assertParse("(1)", asList('(', "1", ')'));
    assertParse("(1 + 2)", asList('(', asList("1", '+', "2"), ')'));
    assertParse("((1))", asList('(', asList('(', "1", ')'), ')'));
    assertParse("((1 + 2))",
        asList('(', asList('(', asList("1", '+', "2"), ')'), ')'));
    assertParse("2 * (3 + 4)",
        asList("2", '*', asList('(', asList("3", '+', "4"), ')')));
    assertParse("(2 + 3) * 4",
        asList(asList('(', asList("2", '+', "3"), ')'), '*', "4"));
  }

  @Test
  public void testEvaluateParenthesis() {
    assertEvaluation("(1)", 1);
    assertEvaluation("(1 + 2)", 3);
    assertEvaluation("((1))", 1);
    assertEvaluation("((1 + 2))", 3);
    assertEvaluation("2 * (3 + 4)", 14);
    assertEvaluation("(2 + 3) * 4", 20);
    assertEvaluation("6 / (2 + 4)", 1);
    assertEvaluation("(2 + 6) / 2", 4);
  }

  @Test
  public void testParsePriority() {
    assertParse("1 * 2 + 3", asList(asList("1", '*', "2"), '+', "3"));
    assertParse("1 + 2 * 3", asList("1", '+', asList("2", '*', "3")));
  }

  @Test
  public void testEvaluatePriority() {
    assertEvaluation("2 * 3 + 4", 10);
    assertEvaluation("2 + 3 * 4", 14);
    assertEvaluation("6 / 3 + 4", 6);
    assertEvaluation("2 + 6 / 2", 5);
  }

  @Test
  public void testParsePostfixAdd() {
    assertParse("0++", asList("0", "++"));
    assertParse("0++++", asList(asList("0", "++"), "++"));
  }

  @Test
  public void testEvaluatePostfixAdd() {
    assertEvaluation("0++", 1);
    assertEvaluation("0++++", 2);
    assertEvaluation("0++++++", 3);
    assertEvaluation("0+++1", 2);
    assertEvaluation("0+++++1", 3);
    assertEvaluation("0+++++++1", 4);
  }

  @Test
  public void testParsePostfixSub() {
    assertParse("0--", asList("0", "--"));
    assertParse("0----", asList(asList("0", "--"), "--"));
  }

  @Test
  public void testEvaluatePostfixSub() {
    assertEvaluation("1--", 0);
    assertEvaluation("2----", 0);
    assertEvaluation("3------", 0);
    assertEvaluation("2---1", 0);
    assertEvaluation("3-----1", 0);
    assertEvaluation("4-------1", 0);
  }

  @Test
  public void testParsePrefixNegate() {
    assertParse("-0", asList('-', "0"));
    assertParse("--0", asList('-', asList('-', "0")));
  }

  @Test
  public void testEvaluatePrefixNegate() {
    assertEvaluation("1", 1);
    assertEvaluation("-1", -1);
    assertEvaluation("--1", 1);
    assertEvaluation("---1", -1);
  }

  @Test
  public void testBuildWithoutGroupsFails() {
    Parser parser = new ExpressionBuilder<>().build();
    assertNotNull(parser);
    Result result = parser.parse("");
    assertTrue(result.isFailure());
  }

  @Test
  public void testBuilderLevelPrimitive() {
    ExpressionBuilder<Integer> builder = new ExpressionBuilder<>();
    builder.primitive(digit().map(ch -> Character.digit((char) ch, 10)));
    Parser parser = builder.build().end();
    assertEquals(Integer.valueOf(5), parser.parse("5").get());
    assertTrue(parser.parse("a").isFailure());
  }

  @Test
  public void testBuilderLevelPrimitiveWithAction() {
    ExpressionBuilder<Integer> builder = new ExpressionBuilder<>();
    builder.primitive(digit().plus().flatten(), (String s) -> Integer.parseInt(s));
    Parser parser = builder.build().end();
    assertEquals(Integer.valueOf(123), parser.parse("123").get());
  }

  @Test
  public void testMultipleBuilderPrimitives() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    builder.primitive(of('a').map(Object::toString));
    builder.primitive(of('b').map(Object::toString));
    Parser parser = builder.build().end();
    assertEquals("a", parser.parse("a").get());
    assertEquals("b", parser.parse("b").get());
    assertTrue(parser.parse("c").isFailure());
  }

  @Test
  public void testEmptyBuildWithOnlyPrimitives() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    builder.primitive(of('a').map(Object::toString));
    Parser parser = builder.build().end();
    assertEquals("a", parser.parse("a").get());
  }

  @Test
  public void testBuilderLoopback() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    builder.primitive(of('a').seq(builder.loopback()).flatten());
    builder.primitive(of('b').map(Object::toString));
    Parser parser = builder.build().end();
    assertEquals("b", parser.parse("b").get());
    assertEquals("ab", parser.parse("ab").get());
    assertEquals("aab", parser.parse("aab").get());
    assertEquals("aaab", parser.parse("aaab").get());
    assertTrue(parser.parse("a").isFailure());
  }

  @Test
  public void testBuilderGetLoopbackGetter() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    assertNotNull(builder.getLoopback());
    assertSame(builder.getLoopback(), builder.loopback());
  }

  @Test
  public void testOptionalGroupBasic() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    builder.primitive(digit().map(Object::toString));
    builder.group()
        .wrapper(of('('), of(')'), (l, v, r) -> "(" + v + ")")
        .optional("∅");
    Parser parser = builder.build().end();
    assertEquals("∅", parser.parse("").get());
    assertEquals("(∅)", parser.parse("()").get());
    assertEquals("1", parser.parse("1").get());
    assertEquals("(1)", parser.parse("(1)").get());
  }

  @Test
  public void testOptionalGroupNull() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    builder.primitive(digit().map(Object::toString));
    builder.group().optional(null);
    Parser parser = builder.build().end();
    assertNull(parser.parse("").get());
    assertEquals("1", parser.parse("1").get());
  }

  @Test(expected = IllegalStateException.class)
  public void testOptionalGroupRepeatedFails() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    ExpressionBuilder.ExpressionGroup<String> group = builder.group();
    group.optional("foo");
    group.optional("bar");
  }

  @Test
  public void testOptionalWithRegexGrammar() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    builder.primitive(noneOf("()!|&?").map(Object::toString));
    builder.group()
        .wrapper(of('('), of(')'), (l, v, r) -> "(" + v + ")")
        .prefix(of('!'), (op, v) -> "!(" + v + ")")
        .postfix(of('?'), (v, op) -> "(" + v + ")?")
        .left(of('|'), (l, op, r) -> "(" + l + "|" + r + ")")
        .right(of('&'), (l, op, r) -> "(" + l + "&" + r + ")");
    builder.group()
        .left(new EpsilonParser(null), (l, op, r) -> "[" + l + r + "]")
        .optional("∅");
    Parser parser = builder.build().end();

    assertEquals("∅", parser.parse("").get());
    assertEquals("a", parser.parse("a").get());
    assertEquals("[ab]", parser.parse("ab").get());
    assertEquals("[[ab]c]", parser.parse("abc").get());
    assertEquals("(a&b)", parser.parse("a&b").get());
    assertEquals("(a&(b&c))", parser.parse("a&b&c").get());
    assertEquals("(a|b)", parser.parse("a|b").get());
    assertEquals("((a|b)|c)", parser.parse("a|b|c").get());
    assertEquals("(a)?", parser.parse("a?").get());
    assertEquals("((a)?)?", parser.parse("a??").get());
    assertEquals("!(a)", parser.parse("!a").get());
    assertEquals("!(!(a))", parser.parse("!!a").get());
    assertEquals("(∅)", parser.parse("()").get());
    assertEquals("(a)", parser.parse("(a)").get());
    assertEquals("([ab])", parser.parse("(ab)").get());
  }

  @Test
  public void testTypedEvaluator() {
    ExpressionBuilder<Double> builder = new ExpressionBuilder<>();
    builder.primitive(digit().plus().seq(of('.')
            .seq(digit().plus()).optional())
        .flatten()
        .trim(), Double::parseDouble);
    builder.group()
        .wrapper(
            of('(').trim(),
            of(')').trim(),
            (l, v, r) -> v);
    builder.group()
        .prefix(of('-').trim(), (op, v) -> -v);
    builder.group()
        .postfix(of("++").trim(), (v, op) -> v + 1.0)
        .postfix(of("--").trim(), (v, op) -> v - 1.0);
    builder.group()
        .right(of('^').trim(),
            (l, op, r) -> Math.pow(l, r));
    builder.group()
        .left(of('*').trim(),
            (l, op, r) -> l * r)
        .left(of('/').trim(),
            (l, op, r) -> l / r);
    builder.group()
        .left(of('+').trim(),
            (l, op, r) -> l + r)
        .left(of('-').trim(),
            (l, op, r) -> l - r);
    Parser typedEvaluator = builder.build().end();

    assertEquals(0.0, (Double) typedEvaluator.parse("0").get(), 1e-5);
    assertEquals(1.2, (Double) typedEvaluator.parse("1.2").get(), 1e-5);
    assertEquals(-1.2, (Double) typedEvaluator.parse("-1.2").get(), 1e-5);
    assertEquals(3.0, (Double) typedEvaluator.parse("1 + 2").get(), 1e-5);
    assertEquals(6.0, (Double) typedEvaluator.parse("1 + 2 + 3").get(), 1e-5);
    assertEquals(-4.0, (Double) typedEvaluator.parse("1 - 2 - 3").get(), 1e-5);
    assertEquals(6.0, (Double) typedEvaluator.parse("2 * 3").get(), 1e-5);
    assertEquals(4.0, (Double) typedEvaluator.parse("12 / 3").get(), 1e-5);
    assertEquals(25.0, (Double) typedEvaluator.parse("100 / 2 / 2").get(), 1e-5);
    assertEquals(8.0, (Double) typedEvaluator.parse("2 ^ 3").get(), 1e-5);
    assertEquals(262144.0, (Double) typedEvaluator.parse("4 ^ 3 ^ 2").get(), 1e-5);
    assertEquals(14.0, (Double) typedEvaluator.parse("2 * (3 + 4)").get(), 1e-5);
    assertEquals(10.0, (Double) typedEvaluator.parse("2 * 3 + 4").get(), 1e-5);
    assertEquals(14.0, (Double) typedEvaluator.parse("2 + 3 * 4").get(), 1e-5);
    assertEquals(1.0, (Double) typedEvaluator.parse("0++").get(), 1e-5);
    assertEquals(2.0, (Double) typedEvaluator.parse("0++++").get(), 1e-5);
    assertEquals(0.0, (Double) typedEvaluator.parse("1--").get(), 1e-5);
    assertEquals(1.0, (Double) typedEvaluator.parse("--1").get(), 1e-5);
  }

  @Test
  public void testFastParseOn() {
    ExpressionBuilder<Double> builder = new ExpressionBuilder<>();
    builder.primitive(digit().plus().flatten(), Double::parseDouble);
    builder.group().left(of('+'), (l, op, r) -> l + r);
    Parser parser = builder.build().end();
    assertEquals(3, parser.fastParseOn("1+2", 0));
    assertEquals(-1, parser.fastParseOn("1+", 0));
  }

  @Test
  public void testBuilderAndGroupPrimitivesCombined() {
    ExpressionBuilder<Integer> builder = new ExpressionBuilder<>();
    builder.primitive(of('a').map(ch -> 1));
    builder.group().primitive(of('b').map(ch -> 2));
    Parser parser = builder.build().end();
    assertEquals(Integer.valueOf(1), parser.parse("a").get());
    assertEquals(Integer.valueOf(2), parser.parse("b").get());
  }

  @Test
  public void testConstructors() {
    assertNotNull(new ExpressionBuilder.ExpressionGroup<>());
    assertNotNull(new ExpressionBuilder.ExpressionGroupBase());
  }

  @Test
  public void testBuildChoice() {
    Parser otherwise = of('x');
    assertSame(otherwise, ExpressionBuilder.buildChoice(Collections.emptyList(), otherwise));
    assertNull(ExpressionBuilder.buildChoice(Collections.emptyList()));
  }

  @Test
  public void testGroupPrimitiveWithNullAction() {
    ExpressionBuilder.ExpressionGroupBase group = new ExpressionBuilder.ExpressionGroupBase();
    group.primitive(of('x'), null);
    Parser parser = group.build(null);
    assertEquals('x', (char) parser.parse("x").get());
  }

  @Test
  public void testGroupWrapperWithNullAction() {
    ExpressionBuilder<String> builder = new ExpressionBuilder<>();
    builder.primitive(of('x').map(Object::toString));
    builder.group().wrapper(of('['), of(']'), (Function<List<Object>, String>) null);
    Parser parser = builder.build().end();
    assertEquals(asList('[', "x", ']'), parser.parse("[x]").get());
  }

  @Test
  public void testGroupBuildNullInner() {
    ExpressionBuilder.ExpressionGroupBase group = new ExpressionBuilder.ExpressionGroupBase();
    assertNull(group.build(null));
  }

  @Test(expected = NullPointerException.class)
  public void testBuilderPrimitiveNullParser() {
    new ExpressionBuilder<>().primitive(null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupPrefixNullParser() {
    new ExpressionBuilder<>().group().prefix(null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupPrefixNullParserWithAction() {
    new ExpressionBuilder<>().group().prefix(null, (op, v) -> v);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupPrefixNullAction() {
    new ExpressionBuilder<String>().group().prefix(of('-'), (BiFunction<Object, String, String>) null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupPostfixNullParser() {
    new ExpressionBuilder<>().group().postfix(null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupPostfixNullParserWithAction() {
    new ExpressionBuilder<>().group().postfix(null, (v, op) -> v);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupPostfixNullAction() {
    new ExpressionBuilder<String>().group().postfix(of('+'), (BiFunction<String, Object, String>) null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupRightNullParser() {
    new ExpressionBuilder<>().group().right(null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupRightNullParserWithAction() {
    new ExpressionBuilder<>().group().right(null, (l, op, r) -> l);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupRightNullAction() {
    new ExpressionBuilder<String>().group().right(of('^'), (Function3<String, Object, String, String>) null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupLeftNullParser() {
    new ExpressionBuilder<>().group().left(null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupLeftNullParserWithAction() {
    new ExpressionBuilder<>().group().left(null, (l, op, r) -> l);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupLeftNullAction() {
    new ExpressionBuilder<String>().group().left(of('+'), (Function3<String, Object, String, String>) null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupWrapperNullLeft() {
    new ExpressionBuilder<>().group().wrapper(null, of(')'));
  }

  @Test(expected = NullPointerException.class)
  public void testGroupWrapperNullRight() {
    new ExpressionBuilder<>().group().wrapper(of('('), null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupWrapperNullLeftWithAction() {
    new ExpressionBuilder<>().group().wrapper(null, of(')'), (l, v, r) -> v);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupWrapperNullRightWithAction() {
    new ExpressionBuilder<>().group().wrapper(of('('), null, (l, v, r) -> v);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupWrapperNullAction() {
    new ExpressionBuilder<String>().group().wrapper(of('('), of(')'), (Function3<Object, String, Object, String>) null);
  }

  @Test(expected = NullPointerException.class)
  public void testGroupBaseNullLoopback() {
    new ExpressionBuilder.ExpressionGroupBase(null);
  }
}

