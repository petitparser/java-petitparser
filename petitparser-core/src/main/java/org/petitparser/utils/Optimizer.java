package org.petitparser.utils;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.combinators.SettableParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Tools to optimize a parser graph.
 */
public class Optimizer {

  private final List<Function<Parser, Parser>> transformers = new ArrayList<>();

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
  public Optimizer removeDelegates() {
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
  public Optimizer removeDuplicates() {
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
   * Transforms the provided parsers using the selected optimizations.
   */
  public Parser transform(Parser parser) {
    Function<Parser, Parser> transformer = transformers.stream()
        .reduce(Function::andThen)
        .orElse(Function.identity());
    return Mirror.of(parser).transform(transformer);
  }
}
