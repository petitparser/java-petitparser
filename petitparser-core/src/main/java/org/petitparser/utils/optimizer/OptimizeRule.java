package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;

import java.util.Objects;
import java.util.function.Function;

/**
 * Encapsulates an optimization or transformation rule applied to a parser node.
 */
@FunctionalInterface
public interface OptimizeRule extends Function<Parser, Parser> {

  /**
   * Transforms the given {@code parser} into an optimized equivalent parser,
   * or returns the parser itself if no optimization is applicable.
   *
   * @param parser the parser node to inspect and potentially optimize
   * @return the optimized parser or original parser
   */
  @Override
  Parser apply(Parser parser);

  /**
   * Transforms the given {@code parser} into an optimized equivalent parser.
   * Defaults to delegating to {@link #apply(Parser)}.
   *
   * @param parser the parser node to inspect and potentially optimize
   * @return the optimized parser or original parser
   */
  default Parser optimize(Parser parser) {
    return apply(parser);
  }

  /**
   * Returns the human-readable name of this optimization rule.
   *
   * @return the rule name
   */
  default String getName() {
    return getClass().getSimpleName();
  }

  /**
   * Resets any state accumulated by this rule across transformation runs.
   */
  default void reset() {}

  /**
   * Adapts a {@link Function} to an {@link OptimizeRule}.
   *
   * @param function the function to wrap
   * @return the optimize rule
   */
  static OptimizeRule of(Function<Parser, Parser> function) {
    Objects.requireNonNull(function, "Undefined function");
    return function instanceof OptimizeRule ? (OptimizeRule) function : function::apply;
  }
}
