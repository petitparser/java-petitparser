package org.petitparser.parser;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An {@link Iterable} that lazily produces match results of a {@link Parser}.
 */
public class MatchesIterable<T> implements Iterable<T> {

  private final Parser parser;
  private final String input;
  private final int start;
  private final boolean overlapping;

  /**
   * Constructs an {@link Iterable} producing matches of {@code parser} on {@code input}.
   *
   * @param parser      the parser to match
   * @param input       the input string to match against
   * @param start       the starting offset within {@code input}
   * @param overlapping whether to produce overlapping matches
   */
  public MatchesIterable(Parser parser, String input, int start, boolean overlapping) {
    this.parser = Objects.requireNonNull(parser, "parser must not be null");
    this.input = Objects.requireNonNull(input, "input must not be null");
    if (start < 0) {
      throw new IndexOutOfBoundsException("start offset must be non-negative: " + start);
    }
    this.start = start;
    this.overlapping = overlapping;
  }

  @Override
  public Iterator<T> iterator() {
    return new MatchesIterator<>(parser, input, start, overlapping);
  }

  @Override
  public Spliterator<T> spliterator() {
    return new MatchesSpliterator<>(parser, input, start, overlapping);
  }

  /**
   * Returns a sequential {@link Stream} over the elements in this {@link Iterable}.
   */
  public Stream<T> stream() {
    return StreamSupport.stream(spliterator(), false);
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

  public boolean isOverlapping() {
    return overlapping;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[parser=" + parser + ", start=" + start +
        ", overlapping=" + overlapping + "]";
  }
}
