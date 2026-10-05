package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.utils.Analyzer;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Identifies left-recursive loops that lead to infinite recursion.
 */
public class LeftRecursionRule extends LinterRule {

  public LeftRecursionRule() {
    super(LinterType.ERROR, "Left recursion");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    Set<Parser> cycleSet = analyzer.cycleSet(parser);
    if (!cycleSet.isEmpty()) {
      callback.accept(new LinterIssue(
          this,
          parser,
          "The parsers directly or indirectly refers to itself without consuming input:\n" +
          formatIterable(cycleSet, 1) + "\n" +
          "This causes an infinite loop when parsing."
      ));
    }
  }
}
