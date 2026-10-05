package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Flattens nested choices {@code [a, [b, c]]} into a single choice {@code [a, b, c]}.
 */
public class FlattenChoiceRule implements OptimizeRule {

  @Override
  public Parser apply(Parser parser) {
    if (parser instanceof ChoiceParser) {
      ChoiceParser choice = (ChoiceParser) parser;
      boolean hasNested = false;
      for (Parser child : choice.getChildren()) {
        if (child instanceof ChoiceParser) {
          hasNested = true;
          break;
        }
      }
      if (hasNested) {
        List<Parser> children = new ArrayList<>();
        flatten(choice, children, new HashSet<>());
        return new ChoiceParser(choice.getFailureJoiner(), children.toArray(new Parser[0]));
      }
    }
    return parser;
  }

  private void flatten(ChoiceParser choice, List<Parser> result, Set<Parser> seen) {
    if (!seen.add(choice)) {
      return;
    }
    for (Parser child : choice.getChildren()) {
      if (child instanceof ChoiceParser) {
        flatten((ChoiceParser) child, result, seen);
      } else {
        result.add(child);
      }
    }
  }
}
