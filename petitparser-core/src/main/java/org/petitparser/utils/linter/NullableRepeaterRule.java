package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.repeating.RepeatingParser;
import org.petitparser.parser.repeating.SeparatedRepeatingParser;
import org.petitparser.utils.Analyzer;

import java.util.function.Consumer;

/**
 * Identifies repeating parsers over nullable delegates that lead to infinite loops.
 */
public class NullableRepeaterRule extends LinterRule {

  public NullableRepeaterRule() {
    super(LinterType.ERROR, "Nullable repeater");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof RepeatingParser) {
      Parser delegate = parser.getChildren().getFirst();
      if (analyzer.isNullable(delegate)) {
        if (parser instanceof SeparatedRepeatingParser) {
          Parser separator = parser.getChildren().get(1);
          if (!analyzer.isNullable(separator)) {
            return;
          }
        }
        callback.accept(new LinterIssue(
            this,
            parser,
            "A repeater that delegates to a nullable parser causes an infinite loop when parsing."
        ));
      }
    }
  }
}
