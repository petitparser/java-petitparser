package org.petitparser;

import static org.junit.Assert.assertEquals;
import static org.petitparser.Chars.character;
import static org.petitparser.Chars.digit;
import static org.petitparser.Parsers.failure;
import static org.petitparser.Parsers.string;

import java.util.List;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.SetableParser;
import org.petitparser.tools.ExpressionBuilder;

import com.google.common.base.Function;

/**
 * Tests {@link ExpressionBuilder}.
 */
public class ExpressionTest {

  private static Parser createParser() {
    SetableParser root = failure("Undefined").setable();
    ExpressionBuilder builder = new ExpressionBuilder();
    builder.group()
      .primitive(character('(').trim()
          .seq(root)
          .seq(character(')').trim())
          .pick(1))
      .primitive(digit().plus()
          .seq(character('.').seq(digit().plus()).optional())
          .flatten().trim().map(Double::parseDouble));
    builder.group()
      .prefix(character('-').trim(), new Function<List<Double>, Double>() {
        @Override
        public Double apply(List<Double> values) {
          return -values.get(1);
        }
      });
    builder.group()
      .postfix(string("++").trim(), new Function<List<Double>, Double>() {
        @Override
        public Double apply(List<Double> values) {
          return values.get(0) + 1;
        }
      })
      .postfix(string("--").trim(), new Function<List<Double>, Double>() {
        @Override
        public Double apply(List<Double> values) {
          return values.get(0) - 1;
        }
      });
    builder.group()
      .right(character('^').trim(), new Function<List<Double>, Double>() {
        @Override
        public Double apply(List<Double> values) {
          return Math.pow(values.get(0), values.get(2));
        }
      });
    builder.group()
      .left(character('*').trim(), new Function<List<Double>, Double>() {
        @Override
        public Double apply(List<Double> values) {
          return values.get(0) * values.get(2);
        }
      })
      .left(character('/').trim(), new Function<List<Double>, Double>() {
        @Override
        public Double apply(List<Double> values) {
          return values.get(0) / values.get(2);
        }
      });
    builder.group()
      .left(character('+').trim(), new Function<List<Double>, Double>() {
        @Override
        public Double apply(List<Double> values) {
          return values.get(0) + values.get(2);
        }
      })
      .left(character('-').trim(), new Function<List<Double>, Double>() {
        @Override
        public Double apply(List<Double> values) {
          return values.get(0) - values.get(2);
        }
      });
    root.set(builder.build());
    return root.end();
  }

  private static final double EPSILON = 1e-5;
  private static final Parser PARSER = createParser();

  private static void assertExpression(String input, double expected) {
    double actual = Parsing.parse(PARSER, input).get();
    assertEquals(expected, actual, EPSILON);
  }

  @Test
  public void testNumber() {
    assertExpression("0", 0);
    assertExpression("0.0", 0);
    assertExpression("1", 1);
    assertExpression("1.2", 1.2);
    assertExpression("34", 34);
    assertExpression("34.7", 34.7);
    assertExpression("56.78", 56.78);
  }

  @Test
  public void testNegativeNumber() {
    assertExpression("-1", -1);
    assertExpression("-1.2", -1.2);
  }

  @Test
  public void testAdd() {
    assertExpression("1 + 2", 3);
    assertExpression("2 + 1", 3);
    assertExpression("1 + 2.3", 3.3);
    assertExpression("2.3 + 1", 3.3);
    assertExpression("1 + -2", -1);
    assertExpression("-2 + 1", -1);
  }

  @Test
  public void testAddMany() {
    assertExpression("1", 1);
    assertExpression("1 + 2", 3);
    assertExpression("1 + 2 + 3", 6);
    assertExpression("1 + 2 + 3 + 4", 10);
    assertExpression("1 + 2 + 3 + 4 + 5", 15);
  }

  @Test
  public void testSub() {
    assertExpression("1 - 2", -1);
    assertExpression("1.2 - 1.2", 0);
    assertExpression("1 - -2", 3);
    assertExpression("-1 - -2", 1);
  }

  @Test
  public void testSubMany() {
    assertExpression("1", 1);
    assertExpression("1 - 2", -1);
    assertExpression("1 - 2 - 3", -4);
    assertExpression("1 - 2 - 3 - 4", -8);
    assertExpression("1 - 2 - 3 - 4 - 5", -13);
  }

  @Test
  public void testMul() {
    assertExpression("2 * 3", 6);
    assertExpression("2 * -4", -8);
  }

  @Test
  public void testMulMany() {
    assertExpression("1 * 2", 2);
    assertExpression("1 * 2 * 3", 6);
    assertExpression("1 * 2 * 3 * 4", 24);
    assertExpression("1 * 2 * 3 * 4 * 5", 120);
  }

  @Test
  public void testDiv() {
    assertExpression("12 / 3", 4);
    assertExpression("-16 / -4", 4);
  }

  @Test
  public void testDivMany() {
    assertExpression("100 / 2", 50);
    assertExpression("100 / 2 / 2", 25);
    assertExpression("100 / 2 / 2 / 5", 5);
    assertExpression("100 / 2 / 2 / 5 / 5", 1);
  }

  @Test
  public void testPow() {
    assertExpression("2 ^ 3", 8);
    assertExpression("-2 ^ 3", -8);
    assertExpression("-2 ^ -3", -0.125);
  }

  @Test
  public void testPowMany() {
    assertExpression("4 ^ 3", 64);
    assertExpression("4 ^ 3 ^ 2", 262144);
    assertExpression("4 ^ 3 ^ 2 ^ 1", 262144);
    assertExpression("4 ^ 3 ^ 2 ^ 1 ^ 0", 262144);
  }

  @Test
  public void testParens() {
    assertExpression("(1)", 1);
    assertExpression("(1 + 2)", 3);
    assertExpression("((1))", 1);
    assertExpression("((1 + 2))", 3);
    assertExpression("2 * (3 + 4)", 14);
    assertExpression("(2 + 3) * 4", 20);
    assertExpression("6 / (2 + 4)", 1);
    assertExpression("(2 + 6) / 2", 4);
  }

  @Test
  public void testPriority() {
    assertExpression("2 * 3 + 4", 10);
    assertExpression("2 + 3 * 4", 14);
    assertExpression("6 / 3 + 4", 6);
    assertExpression("2 + 6 / 2", 5);
  }

  @Test
  public void testPostfixAdd() {
    assertExpression("0++", 1);
    assertExpression("0++++", 2);
    assertExpression("0++++++", 3);
    assertExpression("0+++1", 2);
    assertExpression("0+++++1", 3);
    assertExpression("0+++++++1", 4);
  }

  @Test
  public void testPostfixSub() {
    assertExpression("1--", 0);
    assertExpression("2----", 0);
    assertExpression("3------", 0);
    assertExpression("2---1", 0);
    assertExpression("3-----1", 0);
    assertExpression("4-------1", 0);
  }

  @Test
  public void testPrefixNegate() {
    assertExpression("1", 1);
    assertExpression("-1", -1);
    assertExpression("--1", 1);
    assertExpression("---1", -1);
  }

}
