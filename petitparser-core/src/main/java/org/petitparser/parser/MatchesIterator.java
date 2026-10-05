package org.petitparser.parser;

import org.petitparser.context.Context;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * An {@link Iterator} that lazily produces match results of a {@link Parser}.
 */
public class MatchesIterator<T> implements Iterator<T> {

  private final Parser parser;
  private final String input;
  private final int start;
  private final boolean overlapping;
  private int current;
  private T next;
  private boolean hasNext;

  /**
   * Constructs an {@link Iterator} producing matches of {@code parser} on {@code input}.
   *
   * @param parser      the parser to match
   * @param input       the input string to match against
   * @param start       the starting offset within {@code input}
   * @param overlapping whether to produce overlapping matches
   */
  public MatchesIterator(Parser parser, String input, int start, boolean overlapping) {
    this.parser = Objects.requireNonNull(parser, "parser must not be null");
    this.input = Objects.requireNonNull(input, "input must not be null");
    if (start < 0) {
      throw new IndexOutOfBoundsException("start offset must be non-negative: " + start);
    }
    this.start = start;
    this.current = start;
    this.overlapping = overlapping;
    advance();
  }

  @SuppressWarnings("unchecked")
  private void advance() {
    while (current <= input.length()) {
      int end = parser.fastParseOn(input, current);
      if (end < 0) {
        current++;
      } else {
        next = (T) parser.parseOn(new Context(input, current)).get();
        hasNext = true;
        if (overlapping || current == end) {
          current++;
        } else {
          current = end;
        }
        return;
      }
    }
    next = null;
    hasNext = false;
  }

  @Override
  public boolean hasNext() {
    return hasNext;
  }

  @Override
  public T next() {
    if (!hasNext) {
      throw new NoSuchElementException();
    }
    T result = next;
    advance();
    return result;
  }

  public Parser getParser() {
    return parser;
  }

  public String getInput() {
    return input;
  }

  public int getStart() {
    return start;
  }

  public int getCurrent() {
    return current;
  }

  public boolean isOverlapping() {
    return overlapping;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[parser=" + parser + ", start=" + start +
        ", current=" + current + ", overlapping=" + overlapping + "]";
  }
}
