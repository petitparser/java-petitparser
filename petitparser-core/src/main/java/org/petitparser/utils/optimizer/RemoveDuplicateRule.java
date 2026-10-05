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
  private final boolean clearOnReset;

  public RemoveDuplicateRule() {
    this(new HashSet<>(), true);
  }

  public RemoveDuplicateRule(Set<Parser> uniques) {
    this(uniques, false);
  }

  private RemoveDuplicateRule(Set<Parser> uniques, boolean clearOnReset) {
    this.uniques = Objects.requireNonNull(uniques, "Undefined uniques");
    this.clearOnReset = clearOnReset;
  }

  public Set<Parser> getUniques() {
    return uniques;
  }

  public void clear() {
    uniques.clear();
  }

  @Override
  public void reset() {
    if (clearOnReset) {
      clear();
    }
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
