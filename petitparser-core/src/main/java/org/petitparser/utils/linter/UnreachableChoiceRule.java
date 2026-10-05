package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.utils.Analyzer;

import java.util.List;
import java.util.function.Consumer;

/**
 * Identifies choices located after unconditional matchers.
 */
public class UnreachableChoiceRule extends LinterRule {

  public UnreachableChoiceRule() {
    super(LinterType.WARNING, "Unreachable choice");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof ChoiceParser) {
      List<Parser> children = parser.getChildren();
      for (int i = 0; i < children.size() - 1; i++) {
        if (analyzer.isNullable(children.get(i))) {
          callback.accept(new LinterIssue(
              this,
              parser,
              "The choice at index " + i + " is nullable:\n" +
              " " + i + ": " + children.get(i) + "\n" +
              "thus the choices after that can never be reached and can be removed:\n" +
              formatIterable(children.subList(i + 1, children.size()), i + 1)
          ));
        }
      }
    }
  }
}
