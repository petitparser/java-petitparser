package org.petitparser.utils;

import org.petitparser.context.Context;
import org.petitparser.parser.Parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Visual execution debugger that tracks parser activation and progress through the input.
 */
public final class Progress {

  private Progress() {
  }

  /**
   * Returns a parser that prints progress information to standard output during parsing.
   */
  public static Parser of(Parser root) {
    return progress(root);
  }

  /**
   * Returns a parser that calls the provided {@code observer} with a {@link ProgressFrame}
   * whenever a parser is activated during parsing.
   */
  public static Parser of(Parser root, Consumer<ProgressFrame> observer) {
    return progress(root, observer);
  }

  /**
   * Returns a parser that calls the provided {@code observer} with a {@link ProgressFrame}
   * whenever a parser matching {@code predicate} is activated during parsing.
   */
  public static Parser of(
      Parser root, Predicate<Parser> predicate, Consumer<ProgressFrame> observer) {
    return progress(root, predicate, observer);
  }

  /**
   * Returns a parser that prints progress information to standard output during parsing.
   */
  public static Parser progress(Parser root) {
    return progress(root, System.out::println);
  }

  /**
   * Returns a parser that calls the provided {@code observer} with a {@link ProgressFrame}
   * whenever a parser is activated during parsing.
   */
  public static Parser progress(Parser root, Consumer<ProgressFrame> observer) {
    return on(root, observer);
  }

  /**
   * Returns a parser that calls the provided {@code observer} with a {@link ProgressFrame}
   * whenever a parser matching {@code predicate} is activated during parsing.
   */
  public static Parser progress(
      Parser root, Predicate<Parser> predicate, Consumer<ProgressFrame> observer) {
    return on(root, predicate, observer);
  }

  /**
   * Returns a parser that calls the provided {@code observer} with a {@link ProgressFrame}
   * whenever a parser is activated during parsing.
   */
  public static Parser on(Parser root, Consumer<ProgressFrame> observer) {
    return on(root, parser -> true, observer);
  }

  /**
   * Returns a parser that calls the provided {@code observer} with a {@link ProgressFrame}
   * whenever a parser matching {@code predicate} is activated during parsing.
   */
  public static Parser on(
      Parser root, Predicate<Parser> predicate, Consumer<ProgressFrame> observer) {
    Objects.requireNonNull(root, "root parser must not be null");
    Objects.requireNonNull(predicate, "predicate must not be null");
    Objects.requireNonNull(observer, "observer must not be null");

    ThreadLocal<Deque<ProgressState>> stateHolder = ThreadLocal.withInitial(ArrayDeque::new);
    Parser transformed = Mirror.of(root).transform(parser -> {
      if (predicate.test(parser)) {
        return parser.callCC((continuation, context) -> {
          Deque<ProgressState> stack = stateHolder.get();
          ProgressState current = stack.peek();
          if (current == null) {
            current = new ProgressState();
            stack.push(current);
          }
          ProgressFrame frame = current.step(parser, context);
          observer.accept(frame);
          return continuation.apply(context);
        });
      } else {
        return parser;
      }
    });

    return transformed.callCC((continuation, context) -> {
      Deque<ProgressState> stack = stateHolder.get();
      stack.push(new ProgressState());
      try {
        return continuation.apply(context);
      } finally {
        stack.pop();
        if (stack.isEmpty()) {
          stateHolder.remove();
        }
      }
    });
  }

  /**
   * Internal state tracking progress across parser steps.
   */
  private static class ProgressState {
    private int previousPosition = -1;
    private int maxPosition = -1;

    ProgressFrame step(Parser parser, Context context) {
      int position = context.getPosition();
      boolean isBacktracking = previousPosition >= 0 && position < previousPosition;
      int prev = previousPosition;
      if (position > maxPosition) {
        maxPosition = position;
      }
      int max = maxPosition;
      previousPosition = position;
      return new ProgressFrame(parser, context, position, prev, max, isBacktracking);
    }
  }

  /**
   * Encapsulates the execution data of a progress step.
   */
  public static class ProgressFrame {

    /**
     * The parser being activated.
     */
    public final Parser parser;

    /**
     * The activation context.
     */
    public final Context context;

    /**
     * The current position in the input.
     */
    public final int position;

    /**
     * The position of the previous frame, or -1 if this is the first frame.
     */
    public final int previousPosition;

    /**
     * The highest position reached so far (at or before this frame).
     */
    public final int maxPosition;

    /**
     * Whether this frame jumped backwards relative to the immediately preceding frame.
     */
    public final boolean isBacktracking;

    /**
     * Constructs a progress frame with default backtracking values.
     */
    public ProgressFrame(Parser parser, Context context) {
      this(parser, context, context != null ? context.getPosition() : 0, -1,
          context != null ? context.getPosition() : 0, false);
    }

    /**
     * Constructs a progress frame with explicit backtracking state.
     */
    public ProgressFrame(Parser parser, Context context, boolean isBacktracking) {
      this(parser, context, context != null ? context.getPosition() : 0, -1,
          context != null ? context.getPosition() : 0, isBacktracking);
    }

    /**
     * Constructs a fully specified progress frame.
     */
    public ProgressFrame(
        Parser parser, Context context, int position, int previousPosition,
        int maxPosition, boolean isBacktracking) {
      this.parser = parser;
      this.context = context;
      this.position = position;
      this.previousPosition = previousPosition;
      this.maxPosition = maxPosition;
      this.isBacktracking = isBacktracking;
    }

    public Parser getParser() {
      return parser;
    }

    public Context getContext() {
      return context;
    }

    public int getPosition() {
      return position;
    }

    public int getPreviousPosition() {
      return previousPosition;
    }

    public int getMaxPosition() {
      return maxPosition;
    }

    public boolean isBacktracking() {
      return isBacktracking;
    }

    public boolean isBelowMaxPosition() {
      return maxPosition >= 0 && position < maxPosition;
    }

    public boolean isBacktracked() {
      return isBelowMaxPosition();
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      int count = Math.max(1, position + 1);
      for (int i = 0; i < count; i++) {
        builder.append('*');
      }
      builder.append(' ');
      builder.append(parser);
      return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ProgressFrame that = (ProgressFrame) o;
      return position == that.position &&
          previousPosition == that.previousPosition &&
          maxPosition == that.maxPosition &&
          isBacktracking == that.isBacktracking &&
          Objects.equals(parser, that.parser) &&
          Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
      return Objects.hash(parser, context, position, previousPosition, maxPosition, isBacktracking);
    }
  }
}
