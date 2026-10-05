package org.petitparser.utils;

import org.petitparser.context.Failure;

import java.util.function.BiFunction;

/**
 * Function definition that joins {@link Failure} instances.
 */
@FunctionalInterface
public interface FailureJoiner extends BiFunction<Failure, Failure, Failure> {

  /**
   * Reports the first parse failure observed.
   */
  class SelectFirst implements FailureJoiner {
    @Override
    public Failure apply(Failure first, Failure second) {
      return first;
    }

    @Override
    public boolean equals(Object obj) {
      return obj != null && getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
      return SelectFirst.class.hashCode();
    }
  }

  /**
   * Reports the last parse failure observed (default).
   */
  class SelectLast implements FailureJoiner {
    @Override
    public Failure apply(Failure first, Failure second) {
      return second;
    }

    @Override
    public boolean equals(Object obj) {
      return obj != null && getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
      return SelectLast.class.hashCode();
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

    @Override
    public boolean equals(Object obj) {
      return obj != null && getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
      return SelectFarthest.class.hashCode();
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

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      SelectFarthestJoined other = (SelectFarthestJoined) obj;
      return java.util.Objects.equals(messageJoiner, other.messageJoiner);
    }

    @Override
    public int hashCode() {
      return java.util.Objects.hash(SelectFarthestJoined.class, messageJoiner);
    }
  }
}
