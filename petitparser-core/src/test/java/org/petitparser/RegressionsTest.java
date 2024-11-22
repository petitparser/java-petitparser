package org.petitparser;

import org.junit.Test;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;

import static org.petitparser.Assertions.assertAccept;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Various regressions amd tricky examples.
 */
public class RegressionsTest {

  @Test
  public void testFlattenTrim() {
    Parser parser = of('a').flatten().trim();
    assertSuccess(parser, "a", "a");
    assertSuccess(parser, " a ", "a");
    assertSuccess(parser, "  a  ", "a");
  }

  @Test
  public void testTrimFlatten() {
    Parser parser = of('a').trim().flatten();
    assertSuccess(parser, "a", "a");
    assertSuccess(parser, " a ", " a ");
    assertSuccess(parser, "  a  ", "  a  ");
  }

  // https://github.com/petitparser/java-petitparser/issues/80
  Parser createLine(String command) {
    return StringParser.of(command + " ")
        .seq(CharacterParser.word().plusLazy(Token.NEWLINE_PARSER))
        .seq(Token.NEWLINE_PARSER);
  }

  @Test
  public void testGithubIssue80_before() {
    Parser parser = createLine("line1")
        .seq(createLine("line2").optional())
        .seq(createLine("line3"))
        .end();
    assertAccept(parser, "line1 value\nline3 value\n");
    assertAccept(parser, "line1 value\nline2 value\nline3 value\n");
    assertFailure(parser, "line1 value\nline2 !\nline3 value\n", 12, "line3  expected");
  }

  @Test
  public void testGithubIssue80_after() {
    Parser parser = createLine("line1")
        .seq(
            createLine("line3")
                .or(createLine("line2")
                    .seq(createLine("line3"))))
        .end();
    assertAccept(parser, "line1 value\nline3 value\n");
    assertAccept(parser, "line1 value\nline2 value\nline3 value\n");
    assertFailure(parser, "line1 value\nline2 !\nline3 value\n", 18, "letter or digit expected");
  }
}
