package org.petitparser.utils;

import org.petitparser.parser.Parser;
import org.petitparser.utils.optimizer.CharacterRepeaterRule;
import org.petitparser.utils.optimizer.FlattenChoiceRule;
import org.petitparser.utils.optimizer.OptimizeRule;
import org.petitparser.utils.optimizer.RemoveDelegateRule;
import org.petitparser.utils.optimizer.RemoveDuplicateRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Tools to transform and optimize parser graphs.
 */
public class Optimizer {

  /**
   * All standard optimizer rules in recommended application order.
   */
  public static final List<OptimizeRule> ALL_RULES = List.of(
      new RemoveDelegateRule(),
      new FlattenChoiceRule(),
      new CharacterRepeaterRule(),
      new RemoveDuplicateRule()
  );

  private final List<OptimizeRule> rules = new ArrayList<>();

  public Optimizer() {}

  public Optimizer(OptimizeRule... rules) {
    this(Arrays.asList(rules));
  }

  public Optimizer(Iterable<? extends OptimizeRule> rules) {
    if (rules != null) {
      for (OptimizeRule rule : rules) {
        this.rules.add(Objects.requireNonNull(rule, "Undefined rule"));
      }
    }
  }

  /**
   * Returns an unmodifiable list of the optimization rules configured on this optimizer.
   */
  public List<OptimizeRule> getRules() {
    return Collections.unmodifiableList(rules);
  }

  /**
   * Adds an optimization rule.
   */
  public Optimizer add(OptimizeRule rule) {
    rules.add(Objects.requireNonNull(rule, "Undefined rule"));
    return this;
  }

  /**
   * Adds a generic transformer function.
   */
  public Optimizer add(Function<Parser, Parser> transformer) {
    rules.add(OptimizeRule.of(transformer));
    return this;
  }

  /**
   * Adds multiple optimization rules.
   */
  public Optimizer add(OptimizeRule... rules) {
    return addAll(Arrays.asList(rules));
  }

  /**
   * Adds all specified optimization rules.
   */
  public Optimizer addAll(Iterable<? extends OptimizeRule> rules) {
    for (OptimizeRule rule : rules) {
      add(rule);
    }
    return this;
  }

  /**
   * Adds a rule that removes unnecessary delegates (such as SettableParser or direct DelegateParser).
   */
  public Optimizer removeDelegates() {
    return add(new RemoveDelegateRule());
  }

  /**
   * Adds a rule that collapses duplicate structurally-equal parser instances.
   */
  public Optimizer removeDuplicates() {
    return add(new RemoveDuplicateRule());
  }

  /**
   * Adds a rule that collapses duplicate structurally-equal parser instances with a specific set of seen parsers.
   */
  public Optimizer removeDuplicates(Set<Parser> uniques) {
    return add(new RemoveDuplicateRule(uniques));
  }

  /**
   * Adds a rule that flattens nested choices {@code [a, [b, c]]} into {@code [a, b, c]}.
   */
  public Optimizer flattenChoices() {
    return add(new FlattenChoiceRule());
  }

  /**
   * Adds a rule that transforms {@code FlattenParser(PossessiveRepeatingParser(CharacterParser))} into {@code RepeatingCharacterParser}.
   */
  public Optimizer characterRepeaters() {
    return add(new CharacterRepeaterRule());
  }

  /**
   * Alias for {@link #characterRepeaters()}.
   */
  public Optimizer optimizeCharacterRepeaters() {
    return characterRepeaters();
  }

  /**
   * Configures all standard optimization rules (remove delegates, flatten choices, character repeaters, remove duplicates).
   */
  public Optimizer all() {
    return removeDelegates()
        .flattenChoices()
        .characterRepeaters()
        .removeDuplicates();
  }

  /**
   * Optimizes the provided parser using all standard optimization rules.
   */
  public static Parser optimize(Parser parser) {
    Objects.requireNonNull(parser, "Undefined parser");
    return new Optimizer().all().transform(parser);
  }

  /**
   * Transforms the provided parsers using the selected optimizations.
   */
  public Parser transform(Parser parser) {
    Objects.requireNonNull(parser, "Undefined parser");
    rules.forEach(OptimizeRule::reset);
    Function<Parser, Parser> transformer =
        rules.stream().map(r -> (Function<Parser, Parser>) r).reduce(Function::andThen)
            .orElse(Function.identity());
    return Mirror.of(parser).transform(transformer);
  }
}
