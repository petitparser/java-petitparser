package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;

import org.petitparser.utils.FailureJoiner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Flattens nested choices {@code [a, [b, c]]} into a single choice {@code [a, b, c]}.
 */
public class FlattenChoiceRule implements OptimizeRule {

  @Override
  public Parser apply(Parser parser) {
    if (parser != null && ChoiceParser.class.equals(parser.getClass())) {
      ChoiceParser choice = (ChoiceParser) parser;
      FailureJoiner joiner = choice.getFailureJoiner();
      boolean hasNested = false;
      for (Parser child : choice.getChildren()) {
        if (ChoiceParser.class.equals(child.getClass()) &&
            Objects.equals(joiner, ((ChoiceParser) child).getFailureJoiner())) {
          hasNested = true;
          break;
        }
      }
      if (hasNested) {
        List<Parser> children = new ArrayList<>();
        flatten(choice, joiner, children, new HashSet<>());
        if (children.isEmpty() || children.equals(choice.getChildren())) {
          return choice;
        }
        return new ChoiceParser(joiner, children.toArray(new Parser[0]));
      }
    }
    return parser;
  }

  private void flatten(
      ChoiceParser choice, FailureJoiner expectedJoiner, List<Parser> result, Set<Parser> seen) {
    if (!seen.add(choice)) {
      return;
    }
    for (Parser child : choice.getChildren()) {
      if (ChoiceParser.class.equals(child.getClass()) &&
          Objects.equals(expectedJoiner, ((ChoiceParser) child).getFailureJoiner())) {
        flatten((ChoiceParser) child, expectedJoiner, result, seen);
      } else {
        result.add(child);
      }
    }
  }
}
