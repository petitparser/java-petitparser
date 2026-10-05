package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.actions.ActionParser;
import org.petitparser.parser.actions.CastListParser;
import org.petitparser.parser.actions.CastParser;
import org.petitparser.parser.actions.FlattenParser;
import org.petitparser.parser.actions.PermuteParser;
import org.petitparser.parser.actions.PickParser;
import org.petitparser.parser.actions.TokenParser;
import org.petitparser.parser.actions.WhereParser;
import org.petitparser.utils.Analyzer;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Identifies complex sub-parses whose results are discarded.
 */
public class UnusedResultRule extends LinterRule {

  public UnusedResultRule() {
    super(LinterType.INFO, "Unused result");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof FlattenParser) {
      Set<Parser> deepChildren = analyzer.allChildren(parser);
      Set<Parser> ignoredResults = deepChildren.stream()
          .filter(this::isResultProducing)
          .collect(Collectors.toSet());
      if (!ignoredResults.isEmpty()) {
        List<Parser> path = analyzer.findPath(parser, ignoredResults::contains);
        callback.accept(new LinterIssue(
            this,
            parser,
            "The flatten parser discards the result of its children and " +
            "instead returns the consumed input. Yet this flatten parser " +
            "(indirectly) refers to one or more other parsers that explicitly " +
            "produce a result which is then ignored when called from this context:\n" +
            formatIterable(path, 1) + "\n" +
            "This might point to an inefficient grammar or a possible bug."
        ));
      }
    }
  }

  protected boolean isResultProducing(Parser parser) {
    if (parser instanceof CastParser ||
        parser instanceof CastListParser ||
        parser instanceof FlattenParser ||
        parser instanceof PermuteParser ||
        parser instanceof PickParser ||
        parser instanceof TokenParser ||
        parser instanceof WhereParser) {
      return true;
    }
    if (parser instanceof ActionParser) {
      return !((ActionParser<?, ?>) parser).hasSideEffects();
    }
    return false;
  }
}
