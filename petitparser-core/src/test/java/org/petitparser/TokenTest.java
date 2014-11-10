package org.petitparser;

import org.junit.Test;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Token}.
 */
public class TokenTest {

  private final Parser parser = CharacterParser.any()
      .map(Object::hashCode).token().star();
  private final String buffer = "1\r12\r\n123\n1234";
  private final List<Token> result = parser.parse(buffer).get();

  @Test
  public void testBuffer() {
    Object[] expected = {buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer,
        buffer, buffer, buffer, buffer, buffer};
    Object[] actual = result.stream().map(Token::getBuffer).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  public void testInput() {
    Object[] expected = {"1", "\r", "1", "2", "\r", "\n", "1", "2", "3", "\n", "1", "2", "3", "4"};
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
    Object[] expected = {49, 13, 49, 50, 13, 10, 49, 50, 51, 10, 49, 50, 51, 52};
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
  public void testUnique() {
    Set<Token> uniques = new HashSet<>(result);
    assertEquals(result.size(), uniques.size());
  }

}
