package org.petitparser.utils;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tools to debug a grammar.
 */
public class Debugger {

  /**
   * Returns a parser that calls the consumer with a {@link TraceEvent} whenever a parser is called
   * or returns.
   */
  public Parser trace(Parser source, Consumer<TraceEvent> consumer) {
    int level[] = {0};
    return Mirror.of(source).transform(parser -> parser.callCC((continuation, context) -> {
      consumer.accept(new TraceEvent(TraceEventType.ENTER, parser, context, level[0]));
      level[0]++;
      Result result = continuation.apply(context);
      level[0]--;
      consumer.accept(new TraceEvent(TraceEventType.EXIT, parser, result, level[0]));
      return result;
    }));
  }

  /**
   * The type of the trace event differentiating between activation and returning.
   */
  public static enum TraceEventType {
    ENTER,
    EXIT
  }

  /**
   * The actual trace event holding all the relevant data.
   */
  public static class TraceEvent {

    /**
     * The type of this event.
     */
    public final TraceEventType type;

    /**
     * The parser being traced.
     */
    public final Parser parser;

    /**
     * The current parser context.
     */
    public final Context context;

    /**
     * The nesting level.
     */
    public final int level;

    private TraceEvent(TraceEventType type, Parser parser, Context context, int level) {
      this.type = type;
      this.parser = parser;
      this.context = context;
      this.level = level;
    }

    @Override
    public String toString() {
      return repeat("  ", level) + (TraceEventType.ENTER.equals(type) ? parser : context);
    }
  }

  /**
   * Adds a progress indicator that activates the {@code consumer} whenever input is consumed with
   * the position in the input stream.
   *
   * @category debugging
   */
  public Optimizer addProgressIndicator(Consumer<Integer> consumer) {
    return add(parser -> parser.callCC((continuation, context) -> {
      consumer.accept(context.getPosition());
      return continuation.apply(context);
    }));
  }

  private static String repeat(String string, int count) {
    StringBuilder buffer = new StringBuilder(string.length() * count);
    for (int i = 0; i < count; i++) {
      buffer.append(string);
    }
    return buffer.toString();
  }

  /**
   * Adds a profiler that measures the activation count and time of each parser and provides
   *
   * @category debugging
   */
  public Optimizer profile(Consumer<Profile> consumer) {
    Map<Parser, ProfileBuilder> builders = new LinkedHashMap<>();
    return add(parser -> {
      ProfileBuilder builder = new ProfileBuilder(parser);
      builders.put(parser, builder);
      return parser.callCC((continuation, context) -> {
        builder.start();
        Result result = continuation.apply(context);
        builder.stop();
        return result;
      });
    }).callCC((continuation, context) -> {
      builders.values().stream().forEach(ProfileBuilder::reset);
      Result result = continuation.apply(context);
      builders.values().stream().map(ProfileBuilder::build).forEach(consumer);
      return result;
    });
  }

  /**
   * Simple data holder for the profile information about a parser.
   */
  public static class Profile {

    public final Parser parser;

    public final long activations;

    public final long elapsedNanoseconds;

    private Profile(Parser parser, long activations, long elapsedNanoseconds) {
      this.parser = parser;
      this.activations = activations;
      this.elapsedNanoseconds = elapsedNanoseconds;
    }

    @Override
    public String toString() {
      return activations + "\t" + elapsedNanoseconds + "\t" + parser;
    }
  }

  private static class ProfileBuilder {

    private Parser parser;

    private long activations;

    private long elapsedNanoseconds;

    private int nestCount;

    private long startTime;

    private ProfileBuilder(Parser parser) {
      this.parser = parser;
    }

    private void reset() {
      activations = 0;
      elapsedNanoseconds = 0;
      nestCount = 0;
      startTime = 0;
    }

    private void start() {
      nestCount++;
      if (nestCount == 1) {
        startTime = System.nanoTime();
      }
    }

    private void stop() {
      if (nestCount == 1) {
        elapsedNanoseconds += System.nanoTime() - startTime;
        activations++;
      }
      nestCount--;
    }

    private Profile build() {
      return new Profile(parser, activations, elapsedNanoseconds);
    }
  }

  /**
   * Builds the resulting parser.
   */
  public Parser build() {
    return Mirror.of(parser)
        .transform(transformers.stream()
            .reduce(Function::andThen)
            .orElse(Function.identity()));
  }
}
