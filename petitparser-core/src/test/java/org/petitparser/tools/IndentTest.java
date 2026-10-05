package org.petitparser.tools;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.parser.repeating.Tuple2;
import org.petitparser.parser.repeating.Tuple3;
import org.petitparser.parser.repeating.Tuple4;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;

/**
 * Tests for {@link Indent}.
 */
public class IndentTest {

  static class IndentList extends GrammarDefinition {
    final Indent indent = new Indent();

    IndentList() {
      def("start", Parser.seq(
              ref("newlines").optional(),
              ref("things").optional(),
              ref("newlines").optional(),
              new org.petitparser.parser.combinators.EndOfInputParser("end of input expected"))
          .map((nl1, things, nl2, end) -> things != null ? things : Collections.emptyList()));

      def("things", Parser.seq(
              indent.same(),
              ref("object").or(ref("line")))
          .map((same, value) -> value)
          .plus());

      def("object", Parser.seq(
              ref("key"),
              ref("block").or(ref("inline")))
          .map((key, values) -> {
            Map<String, Object> map = new HashMap<>();
            map.put((String) key, values);
            return map;
          }));

      def("key", Parser.seq(
              CharacterParser.pattern("^ \t\r\n:").plusString(),
              indent.getParser().star(),
              CharacterParser.of(':'),
              indent.getParser().star())
          .map((key, ws1, colon, ws2) -> key));

      def("block", Parser.seq(
              ref("newlines"),
              indent.during(ref("things")))
          .map((newlines, things) -> things));

      def("inline", ref("line"));

      def("line", Parser.seq(
              Parser.newline().neg().plus().flatten(),
              ref("newlines").optional())
          .map((line, newlines) -> line));

      def("whitespaces", indent.getParser().star());

      def("newline", Parser.newline());

      def("newlines", Parser.seq(ref("whitespaces"), ref("newline")).plus());
    }
  }

  private static Map<String, Object> mapOf(String key, Object value) {
    Map<String, Object> map = new HashMap<>();
    map.put(key, value);
    return map;
  }

  @Test
  public void testDefinitionEmpty() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    assertSuccess(parser, "", Collections.emptyList());
    assertSuccess(parser, "\n", Collections.emptyList());
    assertSuccess(parser, "\n\r", Collections.emptyList());
    assertSuccess(parser, "\r", Collections.emptyList());
    assertSuccess(parser, "\n\n", Collections.emptyList());
    assertSuccess(parser, "\n\r\n\r", Collections.emptyList());
    assertSuccess(parser, "\r\r", Collections.emptyList());

    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionNewlineBefore() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    assertSuccess(parser, "\na", Collections.singletonList("a"));
    assertSuccess(parser, "\n\ra", Collections.singletonList("a"));
    assertSuccess(parser, "\ra", Collections.singletonList("a"));
    assertSuccess(parser, "\n\na", Collections.singletonList("a"));
    assertSuccess(parser, "\n\r\n\ra", Collections.singletonList("a"));
    assertSuccess(parser, "\r\ra", Collections.singletonList("a"));

    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionNewlineAfter() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    assertSuccess(parser, "a\n", Collections.singletonList("a"));
    assertSuccess(parser, "a\n\r", Collections.singletonList("a"));
    assertSuccess(parser, "a\r", Collections.singletonList("a"));
    assertSuccess(parser, "a\n\n", Collections.singletonList("a"));
    assertSuccess(parser, "a\n\r\n\r", Collections.singletonList("a"));
    assertSuccess(parser, "a\r\r", Collections.singletonList("a"));

    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionSingleIndent() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    List<?> expected = Collections.singletonList(mapOf("a", Collections.singletonList("b")));
    assertSuccess(parser, "a:\n b", expected);
    assertSuccess(parser, "a:\n\tb", expected);
    assertSuccess(parser, "a:\n \tb", expected);
    assertSuccess(parser, "a:\n\t b", expected);

    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionSameIndent() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    List<?> expected = Collections.singletonList(mapOf("a", Arrays.asList("b", "c")));
    assertSuccess(parser, "a:\n b\n c", expected);
    assertSuccess(parser, "a:\n\tb\n\tc", expected);
    assertSuccess(parser, "a:\n \tb\n \tc", expected);
    assertSuccess(parser, "a:\n\t b\n\t c", expected);

    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionDifferentIndent() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    assertFailure(parser, "a:\n b\n\tc", 6);
    assertFailure(parser, "a:\n\tb\n c", 6);

    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionMissingIndent() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    assertSuccess(parser, "a:\nb", Arrays.asList("a:", "b"));
    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionUnexpectedIndent() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    assertFailure(parser, "a\n b", 2);
    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionSameLevel() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    assertSuccess(parser, "a\nb\nc", Arrays.asList("a", "b", "c"));
    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionInlinedValues() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    List<?> expected = Arrays.asList(mapOf("a", "1"), mapOf("b", "2"), mapOf("c", "3"));
    assertSuccess(parser, "a:1\nb: 2\nc :3", expected);
    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionIncreasing() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    List<?> expected = Collections.singletonList(
        mapOf("a", Collections.singletonList(
            mapOf("b", Collections.singletonList("c")))));
    assertSuccess(parser, "a:\n  b:\n    c", expected);
    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testDefinitionDecreasing() {
    IndentList definition = new IndentList();
    Parser parser = definition.build();

    List<?> expected = Arrays.asList(
        mapOf("a", Collections.singletonList("b")),
        "c");
    assertSuccess(parser, "a:\n\tb\nc", expected);
    assertTrue(definition.indent.getStack().isEmpty());
    assertEquals("", definition.indent.getCurrent());
  }

  @Test
  public void testSuccessRestoresIndentation() {
    Indent indent = new Indent();
    Parser inner = indent.same()
        .then(CharacterParser.of('a'))
        .map((indentStr, ch) -> "" + indentStr + ch);
    Parser parser = indent.during(inner);

    assertSuccess(parser, " a", " a");
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testFailureRollsBackIndentation() {
    Indent indent = new Indent();
    Parser inner = indent.same().seq(CharacterParser.of('a'));
    Parser parser = indent.during(inner);

    assertFailure(parser, " b", 1, "'a' expected");
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testNestedFailureRollsBackEachLevel() {
    Indent indent = new Indent();
    Parser inner = indent.during(indent.same().seq(CharacterParser.of('b')));
    Parser outer = indent.during(indent.same().seq(CharacterParser.of('\n')).seq(inner));

    assertFailure(outer, " \n  c", 4, "'b' expected");
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testChoiceRollbackAllowsAlternatePath() {
    Indent indent = new Indent();
    Parser inner1 = indent.same().seq(CharacterParser.of('a'));
    Parser inner2 = indent.same().seq(CharacterParser.of('b'));
    Parser parser = indent.during(inner1).or(indent.during(inner2));

    assertSuccess(parser, " b", Arrays.asList(" ", 'b'));
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testIncreaseFailureLeavesStateUnchanged() {
    Indent indent = new Indent();
    Parser inner = indent.same().seq(CharacterParser.of('a'));
    Parser parser = indent.during(inner);

    assertFailure(parser, "a", 0, "indented expected");
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testRestoresNonEmptyParentIndentationOnSuccess() {
    Indent indent = new Indent();
    Parser block = indent.during(indent.same().seq(CharacterParser.of('b')));
    Parser parser = indent.during(indent.same().seq(CharacterParser.of('a')).seq(block));

    assertSuccess(parser, "  a   b", Arrays.asList("  ", 'a', Arrays.asList("   ", 'b')));
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testConsecutiveDuringBlocksAtSameLevel() {
    Indent indent = new Indent();
    Parser block1 = indent.during(indent.same().seq(CharacterParser.of('a')));
    Parser block2 = indent.during(indent.same().seq(CharacterParser.of('b')));
    Parser parser = block1.seq(block2);

    assertSuccess(parser, " a b", Arrays.asList(Arrays.asList(" ", 'a'), Arrays.asList(" ", 'b')));
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testAcceptFastParseOnSuccessAndFailure() {
    Indent indent = new Indent();
    Parser inner = indent.same().seq(CharacterParser.of('a'));
    Parser parser = indent.during(inner);

    assertTrue(parser.accept(" a"));
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());

    assertFalse(parser.accept(" b"));
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testCustomIndentationTokenParsersTabs() {
    Indent tabIndent = new Indent(CharacterParser.of('\t'), "tab expected");
    Parser inner = tabIndent.same().seq(CharacterParser.of('x'));
    Parser parser = tabIndent.during(inner);

    assertSuccess(parser, "\tx", Arrays.asList("\t", 'x'));
    assertFailure(parser, " x", 0, "tab expected");
    assertEquals("", tabIndent.getCurrent());
    assertTrue(tabIndent.getStack().isEmpty());
  }

  @Test
  public void testCustomIndentationTokenParsersSpaces() {
    Indent spaceIndent = new Indent(CharacterParser.of(' '), "space expected");
    Parser inner = spaceIndent.same().seq(CharacterParser.of('x'));
    Parser parser = spaceIndent.during(inner);

    assertSuccess(parser, " x", Arrays.asList(" ", 'x'));
    assertFailure(parser, "\tx", 0, "space expected");
    assertEquals("", spaceIndent.getCurrent());
    assertTrue(spaceIndent.getStack().isEmpty());
  }

  @Test
  public void testCustomIndentationTokenParsersString() {
    Indent strict2Indent = new Indent(StringParser.of("  "), "two spaces expected");
    Parser inner = strict2Indent.same().seq(CharacterParser.of('x'));
    Parser parser = strict2Indent.during(inner);

    assertSuccess(parser, "  x", Arrays.asList("  ", 'x'));
    assertFailure(parser, " x", 0, "two spaces expected");
    assertEquals("", strict2Indent.getCurrent());
    assertTrue(strict2Indent.getStack().isEmpty());
  }

  @Test
  public void testDirectSameIncreaseDecrease() {
    Indent indent = new Indent();

    // same() at root matches empty indent
    assertSuccess(indent.same(), "", "");
    assertFailure(indent.same(), " ");

    // increase() requires deeper indentation and pushes to stack
    org.petitparser.context.Result increaseResult = indent.increase().parse("  ");
    assertTrue(increaseResult.isSuccess());
    assertEquals("  ", increaseResult.get());
    assertEquals(0, increaseResult.getPosition());
    assertEquals("  ", indent.getCurrent());
    assertEquals(Collections.singletonList(""), indent.getStack());

    // increase() with smaller/same length fails
    assertFailure(indent.increase(), " ");
    assertFailure(indent.increase(), "  ");

    // test fastParseOn for increase after resetting
    indent.reset();
    assertEquals(0, indent.increase().fastParseOn("  ", 0));
    assertEquals("  ", indent.getCurrent());
    assertEquals(Collections.singletonList(""), indent.getStack());

    // decrease() pops from stack
    org.petitparser.context.Result decreaseResult = indent.decrease().parse("");
    assertTrue(decreaseResult.isSuccess());
    assertEquals(null, decreaseResult.get());
    assertEquals(0, decreaseResult.getPosition());
    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());

    // test fastParseOn for decrease with a pushed stack
    indent.stack.add("parent");
    indent.current = "child";
    assertEquals(0, indent.decrease().fastParseOn("", 0));
    assertEquals("parent", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());

    // decrease() on empty stack fails
    assertFailure(indent.decrease(), "");
  }

  @Test
  public void testReset() {
    Indent indent = new Indent();
    indent.stack.add("parent");
    indent.current = "child";

    assertEquals("child", indent.getCurrent());
    assertEquals(Collections.singletonList("parent"), indent.getStack());

    indent.reset();

    assertEquals("", indent.getCurrent());
    assertTrue(indent.getStack().isEmpty());
  }

  @Test
  public void testConstructorsAndGetters() {
    Indent defaultIndent = new Indent();
    assertNotNull(defaultIndent.getParser());
    assertEquals("indented expected", defaultIndent.getMessage());
    assertTrue(defaultIndent.toString().contains("Indent"));

    Indent customParser = new Indent(CharacterParser.of(' '));
    assertNotNull(customParser.getParser());
    assertEquals("indented expected", customParser.getMessage());

    Indent customMessage = new Indent("custom message");
    assertEquals("custom message", customMessage.getMessage());

    Indent customBoth = new Indent(CharacterParser.of('\t'), "tab message");
    assertNotNull(customBoth.getParser());
    assertEquals("tab message", customBoth.getMessage());

    Indent nullBoth = new Indent(null, null);
    assertNotNull(nullBoth.getParser());
    assertEquals("indented expected", nullBoth.getMessage());
  }

  @Test
  public void testDuringNullThrows() {
    Indent indent = new Indent();
    try {
      indent.during(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals("Undefined inner parser", e.getMessage());
    }
  }
}
