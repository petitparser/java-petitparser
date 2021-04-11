package org.petitparser.tools;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.StringParser.of;

import org.junit.Before;
import org.junit.Test;
import org.petitparser.parser.Parser;

import java.util.List;

/**
 * Tests {@link ExpressionBuilder}.
 */
public class ExpressionBuilderTest {

  Parser parser;

  @Before
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
    ExpressionBuilder builder = new ExpressionBuilder();
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
        .right(of('^').trim(), (List<Double> values) -> Math.pow(values.get(0), values.get(2)));
    builder.group()
        .left(of('*').trim(), (List<Double> values) -> values.get(0) * values.get(2))
        .left(of('/').trim(), (List<Double> values) -> values.get(0) / values.get(2));
    builder.group()
        .left(of('+').trim(), (List<Double> values) -> values.get(0) + values.get(2))
        .left(of('-').trim(), (List<Double> values) -> values.get(0) - values.get(2));
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
}
