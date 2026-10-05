package org.petitparser.context;

import org.petitparser.parser.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * A immutable token represents a parsed part of the input.
 *
 * <p>The token holds the resulting value of the input, the input buffer, and
 * the start and stop position in the input buffer. It provides many convenience
 * methods to access the state of the token.
 */
public class Token {

  /**
   * The backing buffer of the token.
   */
  private final String buffer;

  /**
   * The start position of the token in the buffer.
   */
  private final int start;

  /**
   * The stop position of the token in the buffer.
   */
  private final int stop;

  /**
   * The parsed value of the token.
   */
  private final Object value;

  /**
   * Constructs a token from the parsed value, the input buffer, and the start
   * and stop position in the input buffer.
   */
  public Token(String buffer, int start, int stop, Object value) {
    this.buffer = buffer;
    this.start = start;
    this.stop = stop;
    this.value = value;
  }

  /**
   * The backing buffer of the token.
   */
  public String getBuffer() {
    return buffer;
  }

  /**
   * The consumed input of the token.
   */
  public String getInput() {
    return buffer.substring(start, stop);
  }

  /**
   * The consumed input length of the token.
   */
  public int getLength() {
    return stop - start;
  }

  /**
   * The start position of the token in the buffer.
   */
  public int getStart() {
    return start;
  }

  /**
   * The stop position of the token in the buffer.
   */
  public int getStop() {
    return stop;
  }

  /**
   * The resulting value of the token.
   */
  @SuppressWarnings("unchecked")
  public <T> T getValue() {
    return (T) value;
  }

  /**
   * The line number of the token.
   */
  public int getLine() {
    return lineAndColumnOf(buffer, start)[0];
  }

  /**
   * The column number of this token.
   */
  public int getColumn() {
    return lineAndColumnOf(buffer, start)[1];
  }

  @Override
  public String toString() {
    return "Token[" + positionString(buffer, start) + "]: " + value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Token token = (Token) other;
    if (start != token.start) {
      return false;
    }
    if (stop != token.stop) {
      return false;
    }
    if (!Objects.equals(buffer, token.buffer)) {
      return false;
    }
    if (!Objects.equals(value, token.value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, buffer, start, stop);
  }

  /**
   * Returns a parser for that detects newlines platform independently.
   */
  public static final Parser NEWLINE_PARSER =
      of('\n').or(of('\r').seq(of('\n').optional()));

  /**
   * Converts the {@code position} index in a {@code buffer} to a line and
   * column tuple.
   */
  public static int[] lineAndColumnOf(String buffer, int position) {
    List<Token> tokens = NEWLINE_PARSER.token().matchesSkipping(buffer);
    int line = 1, offset = 0;
    for (Token token : tokens) {
      if (position < token.stop) {
        return new int[]{line, position - offset + 1};
      }
      line++;
      offset = token.stop;
    }
    return new int[]{line, position - offset + 1};
  }

  /**
   * Returns a human readable string representing the {@code position} index in a
   * {@code buffer} formatted as {@code "line:column"}.
   */
  public static String positionString(String buffer, int position) {
    int[] lineAndColumn = lineAndColumnOf(buffer, position);
    return lineAndColumn[0] + ":" + lineAndColumn[1];
  }

  /**
   * Combines multiple tokens into a single token with the list of their values.
   *
   * @param tokens the tokens to combine.
   * @return a single combined token.
   * @throws IllegalArgumentException if tokens is empty or tokens do not share the same buffer.
   */
  public static Token join(Token... tokens) {
    return join(Arrays.asList(Objects.requireNonNull(tokens, "Undefined tokens")));
  }

  /**
   * Combines multiple tokens into a single token with the list of their values.
   *
   * @param tokens the tokens to combine.
   * @return a single combined token.
   * @throws IllegalArgumentException if tokens is empty or tokens do not share the same buffer.
   */
  public static Token join(Iterable<Token> tokens) {
    Iterator<Token> iterator = Objects.requireNonNull(tokens, "Undefined tokens").iterator();
    if (!iterator.hasNext()) {
      throw new IllegalArgumentException("Require at least one token");
    }
    Token first = Objects.requireNonNull(iterator.next(), "Undefined token");
    List<Object> values = new ArrayList<>();
    values.add(first.getValue());
    String buffer = first.getBuffer();
    int start = first.getStart();
    int stop = first.getStop();
    while (iterator.hasNext()) {
      Token current = Objects.requireNonNull(iterator.next(), "Undefined token");
      if (!Objects.equals(buffer, current.getBuffer())) {
        throw new IllegalArgumentException("Tokens do not use the same buffer");
      }
      values.add(current.getValue());
      start = Math.min(start, current.getStart());
      stop = Math.max(stop, current.getStop());
    }
    return new Token(buffer, start, stop, values);
  }

  /**
   * Combines multiple tokens into a single token with a custom value.
   *
   * @param tokens the tokens to combine.
   * @param value the custom value of the resulting token.
   * @return a single combined token.
   * @throws IllegalArgumentException if tokens is empty or tokens do not share the same buffer.
   */
  public static Token join(Iterable<Token> tokens, Object value) {
    Iterator<Token> iterator = Objects.requireNonNull(tokens, "Undefined tokens").iterator();
    if (!iterator.hasNext()) {
      throw new IllegalArgumentException("Require at least one token");
    }
    Token first = Objects.requireNonNull(iterator.next(), "Undefined token");
    String buffer = first.getBuffer();
    int start = first.getStart();
    int stop = first.getStop();
    while (iterator.hasNext()) {
      Token current = Objects.requireNonNull(iterator.next(), "Undefined token");
      if (!Objects.equals(buffer, current.getBuffer())) {
        throw new IllegalArgumentException("Tokens do not use the same buffer");
      }
      start = Math.min(start, current.getStart());
      stop = Math.max(stop, current.getStop());
    }
    return new Token(buffer, start, stop, value);
  }
}
