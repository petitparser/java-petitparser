package org.petitparser.utils;

import org.petitparser.context.Failure;

import java.util.function.BiFunction;

/** Function definition that joins {@link Failure} instances. */
@FunctionalInterface
public interface FailureJoiner extends BiFunction<Failure, Failure, Failure> {

  /** Reports the first parse failure observed. */
  class SelectFirst implements FailureJoiner {
    @Override
    public Failure apply(Failure first, Failure second) {
      return first;
    }
  }

  /** Reports the last parse failure observed (default). */
  class SelectLast implements FailureJoiner {
    @Override
    public Failure apply(Failure first, Failure second) {
      return second;
    }
  }

  /**
   * Reports the parser failure farthest down in the input string, preferring
   * later failures over earlier ones.
   */
  class SelectFarthest implements FailureJoiner {
    @Override
    public Failure apply(Failure first, Failure second) {
      return first.getPosition() <= second.getPosition() ? second : first;
    }
  }

  /**
   * Reports the parser failure farthest down in the input string, joining
   * error messages at the same position.
   */
  class SelectFarthestJoined implements FailureJoiner {
    protected final String messageJoiner;

    public SelectFarthestJoined() {
      this(" OR ");
    }

    public SelectFarthestJoined(String messageJoiner) {
      this.messageJoiner = messageJoiner;
    }

    @Override
    public Failure apply(Failure first, Failure second) {
      return first.getPosition() > second.getPosition()
          ? first
          : first.getPosition() < second.getPosition()
          ? second
          :
          first.failure(first.getMessage() + messageJoiner + second.getMessage());
    }
  }
}
