package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.FailureParser;
import org.petitparser.utils.Analyzer;

import java.util.function.Consumer;

/**
 * Identifies {@link SettableParser} left in undefined state.
 */
public class UnresolvedSettableRule extends LinterRule {

  public UnresolvedSettableRule() {
    super(LinterType.ERROR, "Unresolved settable");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof SettableParser) {
      Parser delegate = parser.getChildren().get(0);
      if (delegate instanceof FailureParser) {
        callback.accept(new LinterIssue(
            this,
            parser,
            "This error is typically a bug in the code where a recursive grammar was created with `undefined()` that has not been resolved."
        ));
      }
    }
  }
}
