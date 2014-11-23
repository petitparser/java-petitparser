package org.petitparser.utils;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.combinators.SettableParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by renggli on 20/11/14.
 */
public class Optimizer {

  /**
   * Constructs a transformer of the provided {@code parser}.
   */
  public static Optimizer of(Parser parser) {
    return new Optimizer(parser);
  }

  private final Parser parser;
  private final List<Function<Parser, Parser>> transformers = new ArrayList<>();

  public Optimizer(Parser parser) {
    this.parser = Objects.requireNonNull(parser);
  }

  /**
   * Adds a generic parser transformer.
   */
  public Optimizer add(Function<Parser, Parser> transformer) {
    transformers.add(transformer);
    return this;
  }

  /**
   * Adds a parser transformer that removes unnecessary delegates.
   */
  public Optimizer addDelegateRemoval() {
    return add(parser -> {
      while (DelegateParser.class.equals(parser.getClass()) ||
          SettableParser.class.equals(parser.getClass())) {
        parser = parser.getChildren().get(0);
      }
      return parser;
    });
  }

  /**
   * Adds a parser transformer that collapses unnecessary copies of parsers.
   */
  public Optimizer addDuplicateRemoval() {
    Set<Parser> uniques = new HashSet<>();
    return add(parser -> {
      Optional<Parser> target = uniques.stream()
          .filter(each -> parser != each && parser.isEqualTo(each))
          .findFirst();
      if (target.isPresent()) {
        return target.get();
      } else {
        uniques.add(parser);
        return parser;
      }
    });
  }

  /**
   * Adds a tracer that activates the {@code consumer} with each activated parser and its result.
   *
   * @category debugging
   */
  public Optimizer addTracer(BiConsumer<Parser, Context> onEnter, BiConsumer<Parser, Context> onExit) {
    int level[] = {0};
    return add(parser -> parser.callCC((continuation, context) -> {
      onEnter.accept(parser, context);
      level[0]++;
      Result result = continuation.apply(context);
      level[0]--;
      onEnter.accept(parser, context);
      return result;
    }));
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

  private String repeat(String string, int count) {
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
