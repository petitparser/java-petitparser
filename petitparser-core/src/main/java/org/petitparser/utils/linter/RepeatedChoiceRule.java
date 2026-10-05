package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.utils.Analyzer;

import java.util.List;
import java.util.function.Consumer;

/**
 * Identifies duplicate branches in a choice parser.
 */
public class RepeatedChoiceRule extends LinterRule {

  public RepeatedChoiceRule() {
    super(LinterType.WARNING, "Repeated choice");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof ChoiceParser) {
      List<Parser> children = parser.getChildren();
      for (int i = 0; i < children.size(); i++) {
        for (int j = i + 1; j < children.size(); j++) {
          if (children.get(i).isEqualTo(children.get(j))) {
            callback.accept(new LinterIssue(
                this,
                parser,
                "The choices at index " + i + " and " + j + " are identical:\n" +
                " " + i + ": " + children.get(i) + "\n" +
                " " + j + ": " + children.get(j) + "\n" +
                "The second choice can never succeed and can therefore be removed."
            ));
          }
        }
      }
    }
  }
}
