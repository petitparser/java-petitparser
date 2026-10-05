package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.utils.Analyzer;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Identifies choices where a prefix choice shadows a later choice.
 */
public class OverlappingChoiceRule extends LinterRule {

  public OverlappingChoiceRule() {
    super(LinterType.INFO, "Overlapping choice");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof ChoiceParser) {
      List<Parser> children = parser.getChildren();
      for (int i = 0; i < children.size(); i++) {
        Set<Parser> firstI = analyzer.firstSet(children.get(i));
        for (int j = i + 1; j < children.size(); j++) {
          Set<Parser> firstJ = analyzer.firstSet(children.get(j));
          if (!firstI.isEmpty() && isParserIterableEqual(firstI, firstJ)) {
            callback.accept(new LinterIssue(
                this,
                parser,
                "The choices at index " + i + " and " + j + " have overlapping first-sets, " +
                "which can be an indication of an inefficient grammar:\n" +
                formatIterable(firstI) + "\n" +
                "If possible, try extracting common prefixes from choices."
            ));
          }
        }
      }
    }
  }
}
