package org.petitparser;

import org.junit.Test;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.petitparser.parser.primitive.CharacterParser.any;

/**
 * Tests {@link Token}.
 */
public class TokenTest {

  private final Parser parser = any().map(Object::hashCode).token().star();
  private final String buffer = "1\r12\r\n123\n1234";
  private final List<Token> result = parser.parse(buffer).get();

  @Test
  public void testBuffer() {
    Object[] expected =
        {buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer,
            buffer, buffer, buffer, buffer, buffer};
    Object[] actual = result.stream().map(Token::getBuffer).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testInput() {
    Object[] expected =
        {"1", "\r", "1", "2", "\r", "\n", "1", "2", "3", "\n", "1", "2", "3",
            "4"};
    Object[] actual = result.stream().map(Token::getInput).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testLength() {
    Object[] expected = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    Object[] actual = result.stream().map(Token::getLength).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testStart() {
    Object[] expected = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    Object[] actual = result.stream().map(Token::getStart).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testStop() {
    Object[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
    Object[] actual = result.stream().map(Token::getStop).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testValue() {
    Object[] expected =
        {49, 13, 49, 50, 13, 10, 49, 50, 51, 10, 49, 50, 51, 52};
    Object[] actual = result.stream().map(Token::getValue).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testLine() {
    Object[] expected = {1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4};
    Object[] actual = result.stream().map(Token::getLine).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testColumn() {
    Object[] expected = {1, 2, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4};
    Object[] actual = result.stream().map(Token::getColumn).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testString() {
    Object[] expected =
        {"Token[1:1]: 49", "Token[1:2]: 13", "Token[2:1]: 49", "Token[2:2]: 50",
            "Token[2:3]: 13", "Token[2:4]: 10", "Token[3:1]: 49",
            "Token[3:2]: 50", "Token[3:3]: 51", "Token[3:4]: 10",
            "Token[4:1]: 49", "Token[4:2]: 50", "Token[4:3]: 51",
            "Token[4:4]: 52"};
    Object[] actual = result.stream().map(Token::toString).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testHashCode() {
    Set<Integer> uniques =
        result.stream().map(Token::hashCode).collect(Collectors.toSet());
    assertEquals(result.size(), uniques.size());
  }

  @Test
  public void testEquals() {
    for (int i = 0; i < result.size(); i++) {
      Token first = result.get(i);
      for (int j = 0; j < result.size(); j++) {
        Token second = result.get(j);
        if (i == j) {
          assertEquals(first, second);
        } else {
          assertNotEquals(first, second);
        }
      }
      assertEquals(first,
          new Token(first.getBuffer(), first.getStart(), first.getStop(),
              first.getValue()));
      assertNotEquals(first, null);
      assertNotEquals(first, "Some random string");
      assertNotEquals(first,
          new Token("", first.getStart(), first.getStop(), first.getValue()));
      assertNotEquals(first,
          new Token(first.getBuffer(), first.getStart() + 1, first.getStop(),
              first.getValue()));
      assertNotEquals(first,
          new Token(first.getBuffer(), first.getStart(), first.getStop() + 1,
              first.getValue()));
      assertNotEquals(first,
          new Token(first.getBuffer(), first.getStart(), first.getStop(),
              null));
    }
  }

  @Test
  public void testPositionString() {
    String text = "a\nb";
    assertEquals("1:1", Token.positionString(text, 0));
    assertEquals("1:2", Token.positionString(text, 1));
    assertEquals("2:1", Token.positionString(text, 2));
    assertEquals("2:2", Token.positionString(text, 3));
  }

  @Test
  public void testJoin() {
    Token t1 = new Token("abc", 0, 1, 'a');
    Token t2 = new Token("abc", 1, 2, 'b');
    Token joined = Token.join(Arrays.asList(t1, t2));
    assertEquals("abc", joined.getBuffer());
    assertEquals(0, joined.getStart());
    assertEquals(2, joined.getStop());
    assertEquals("ab", joined.getInput());
    assertEquals(Arrays.asList('a', 'b'), joined.getValue());

    Token single = Token.join(Collections.singletonList(t1));
    assertEquals("abc", single.getBuffer());
    assertEquals(0, single.getStart());
    assertEquals(1, single.getStop());
    assertEquals(Collections.singletonList('a'), single.getValue());

    Token varargs = Token.join(t1, t2);
    assertEquals(joined, varargs);

    Token customValue = Token.join(Arrays.asList(t1, t2), "ab");
    assertEquals("abc", customValue.getBuffer());
    assertEquals(0, customValue.getStart());
    assertEquals(2, customValue.getStop());
    assertEquals("ab", customValue.getValue());
  }

  @Test
  public void testJoinErrors() {
    try {
      Token.join(Collections.emptyList());
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // expected
    }

    try {
      Token.join(Collections.emptyList(), "value");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // expected
    }

    try {
      Token.join((Iterable<Token>) null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }

    try {
      Token.join((Token[]) null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }

    try {
      Token t1 = new Token("abc", 0, 1, 'a');
      Token.join(Arrays.asList(t1, null));
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }

    try {
      Token t1 = new Token("abc", 0, 1, 'a');
      Token t2 = new Token("xyz", 0, 1, 'x');
      Token.join(Arrays.asList(t1, t2));
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // expected
    }

    try {
      Token t1 = new Token("abc", 0, 1, 'a');
      Token t2 = new Token("xyz", 0, 1, 'x');
      Token.join(Arrays.asList(t1, t2), "value");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // expected
    }
  }

  @Test
  public void testToPositionString() {
    Token token = new Token("foo\nbar", 4, 7, "bar");
    assertEquals("2:1", token.toPositionString());
    assertEquals(Token.positionString("foo\nbar", 4), token.toPositionString());
  }

  @Test
  public void testTokenParserFastParse() {
    Parser tokenParser = any().token();
    assertEquals(1, tokenParser.fastParseOn("a", 0));
    assertEquals(-1, tokenParser.fastParseOn("", 0));
  }
}
