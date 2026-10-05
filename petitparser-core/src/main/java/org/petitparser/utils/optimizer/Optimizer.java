package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Tools to transform and optimize parser graphs.
 */
public class Optimizer extends org.petitparser.utils.Optimizer {

  /**
   * All standard optimizer rules in recommended application order.
   */
  public static final List<OptimizeRule> ALL_RULES = List.of(
      new RemoveDelegateRule(),
      new FlattenChoiceRule(),
      new CharacterRepeaterRule(),
      new RemoveDuplicateRule()
  );

  public Optimizer() {
    super();
  }

  @Override
  public Optimizer add(OptimizeRule rule) {
    super.add(rule);
    return this;
  }

  @Override
  public Optimizer add(Function<Parser, Parser> transformer) {
    super.add(transformer);
    return this;
  }

  @Override
  public Optimizer add(OptimizeRule... rules) {
    super.add(rules);
    return this;
  }

  @Override
  public Optimizer addAll(Iterable<? extends OptimizeRule> rules) {
    super.addAll(rules);
    return this;
  }

  @Override
  public Optimizer removeDelegates() {
    super.removeDelegates();
    return this;
  }

  @Override
  public Optimizer removeDuplicates() {
    super.removeDuplicates();
    return this;
  }

  @Override
  public Optimizer removeDuplicates(Set<Parser> uniques) {
    super.removeDuplicates(uniques);
    return this;
  }

  @Override
  public Optimizer flattenChoices() {
    super.flattenChoices();
    return this;
  }

  @Override
  public Optimizer characterRepeaters() {
    super.characterRepeaters();
    return this;
  }

  @Override
  public Optimizer optimizeCharacterRepeaters() {
    super.optimizeCharacterRepeaters();
    return this;
  }

  @Override
  public Optimizer all() {
    super.all();
    return this;
  }
}
