package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Collapses structurally equal duplicate parser instances in the grammar graph to point to a single instance.
 */
public class RemoveDuplicateRule implements OptimizeRule {

  private final Set<Parser> uniques;

  public RemoveDuplicateRule() {
    this(new HashSet<>());
  }

  public RemoveDuplicateRule(Set<Parser> uniques) {
    this.uniques = Objects.requireNonNull(uniques, "Undefined uniques");
  }

  public Set<Parser> getUniques() {
    return uniques;
  }

  public void clear() {
    uniques.clear();
  }

  @Override
  public Parser apply(Parser parser) {
    if (parser == null) {
      return null;
    }
    for (Parser each : uniques) {
      if (parser != each && parser.isEqualTo(each)) {
        return each;
      }
    }
    uniques.add(parser);
    return parser;
  }
}
