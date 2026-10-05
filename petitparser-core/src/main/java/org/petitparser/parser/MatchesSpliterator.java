package org.petitparser.parser;

import org.petitparser.context.Context;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A {@link Spliterator} that lazily produces match results of a {@link Parser}.
 */
public class MatchesSpliterator<T> implements Spliterator<T> {

  private final Parser parser;
  private final String input;
  private final int start;
  private final boolean overlapping;
  private int current;

  /**
   * Constructs a {@link Spliterator} producing matches of {@code parser} on {@code input}.
   *
   * @param parser      the parser to match
   * @param input       the input string to match against
   * @param start       the starting offset within {@code input}
   * @param overlapping whether to produce overlapping matches
   */
  public MatchesSpliterator(Parser parser, String input, int start, boolean overlapping) {
    this.parser = Objects.requireNonNull(parser, "parser must not be null");
    this.input = Objects.requireNonNull(input, "input must not be null");
    if (start < 0) {
      throw new IndexOutOfBoundsException("start offset must be non-negative: " + start);
    }
    this.start = start;
    this.current = start;
    this.overlapping = overlapping;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean tryAdvance(Consumer<? super T> action) {
    Objects.requireNonNull(action, "action must not be null");
    while (current <= input.length()) {
      int end = parser.fastParseOn(input, current);
      if (end < 0) {
        current++;
      } else {
        T value = (T) parser.parseOn(new Context(input, current)).get();
        if (overlapping || current == end) {
          current++;
        } else {
          current = end;
        }
        action.accept(value);
        return true;
      }
    }
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void forEachRemaining(Consumer<? super T> action) {
    Objects.requireNonNull(action, "action must not be null");
    while (current <= input.length()) {
      int end = parser.fastParseOn(input, current);
      if (end < 0) {
        current++;
      } else {
        T value = (T) parser.parseOn(new Context(input, current)).get();
        if (overlapping || current == end) {
          current++;
        } else {
          current = end;
        }
        action.accept(value);
      }
    }
  }

  @Override
  public Spliterator<T> trySplit() {
    return null;
  }

  @Override
  public long estimateSize() {
    return Long.MAX_VALUE;
  }

  @Override
  public int characteristics() {
    return Spliterator.ORDERED;
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
