package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.utils.Analyzer;

import java.util.List;
import java.util.function.Consumer;

/**
 * Identifies nested choice combinators.
 */
public class NestedChoiceRule extends LinterRule {

  public NestedChoiceRule() {
    super(LinterType.INFO, "Nested choice");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof ChoiceParser) {
      List<Parser> children = parser.getChildren();
      for (int i = 0; i < children.size(); i++) {
        Parser child = children.get(i);
        if (child instanceof ChoiceParser) {
          callback.accept(new LinterIssue(
              this,
              parser,
              "The choice at index " + i + " is another choice (" + child + ") that adds " +
              "unnecessary overhead that can be avoided by flattening it into the parent."
          ));
        }
      }
    }
  }
}
