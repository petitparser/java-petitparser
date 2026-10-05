package org.petitparser;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.tools.ExpressionBuilder;
import org.petitparser.tools.GrammarDefinition;
import org.petitparser.tools.GrammarParser;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Verifies raw-type compilation and runtime compatibility for legacy codebases.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class RawTypeCompatibilityTest {

  @Test
  public void testRawParserCombinators() {
    Parser a = CharacterParser.of('a');
    Parser b = CharacterParser.of('b');

    Parser seq = a.seq(b);
    Parser or = a.or(b);
    Parser star = a.star();
    Parser plus = a.plus();
    Parser optional = a.optional();
    Parser not = a.not();
    Parser and = a.and();
    Parser trim = a.trim();
    Parser flatten = a.flatten();
    Parser token = a.token();
    Parser separatedBy = a.separatedBy(b);
    Parser delimitedBy = a.delimitedBy(b);
    Parser skip = a.skip(b, b);
    Parser labeled = a.labeled("label");
    Parser cast = a.cast();
    Parser pick = a.seq(b).pick(0);
    Parser permute = a.seq(b).permute(1, 0);

    assertNotNull(seq);
    assertNotNull(or);
    assertNotNull(star);
    assertNotNull(plus);
    assertNotNull(optional);
    assertNotNull(not);
    assertNotNull(and);
    assertNotNull(trim);
    assertNotNull(flatten);
    assertNotNull(token);
    assertNotNull(separatedBy);
    assertNotNull(delimitedBy);
    assertNotNull(skip);
    assertNotNull(labeled);
    assertNotNull(cast);
    assertNotNull(pick);
    assertNotNull(permute);
  }

  @Test
  public void testRawParseAndResult() {
    Parser parser = CharacterParser.digit().plus().flatten();
    Context context = new Context("123", 0);
    Result result = parser.parseOn(context);

    assertTrue(result.isSuccess());
    assertEquals("123", result.get());
    assertEquals(3, result.getPosition());

    Result quickResult = parser.parse("456");
    assertTrue(quickResult.isSuccess());
    assertEquals("456", quickResult.get());
  }

  @Test
  public void testRawGrammarDefinitionAndParser() {
    GrammarDefinition definition = new GrammarDefinition() {
      {
        def("start", ref("number"));
        def("number", CharacterParser.digit().plus().flatten());
      }
    };

    GrammarParser parser = new GrammarParser(definition);
    Result result = parser.parse("789");
    assertTrue(result.isSuccess());
    assertEquals("789", result.get());
  }

  @Test
  public void testRawExpressionBuilder() {
    ExpressionBuilder builder = new ExpressionBuilder();
    builder.group()
        .primitive(CharacterParser.digit().plus().flatten()
            .map(s -> Integer.parseInt((String) s)));
    builder.group()
        .left(CharacterParser.of('+').trim(), (List values) -> (Integer) values.get(0) + (Integer) values.get(2));

    Parser parser = builder.build();
    Result result = parser.parse("1 + 2 + 3");
    assertTrue(result.isSuccess());
    assertEquals(Integer.valueOf(6), result.get());
  }
}
