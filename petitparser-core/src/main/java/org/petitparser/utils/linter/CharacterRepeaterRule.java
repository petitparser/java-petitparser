package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.actions.FlattenParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.repeating.PossessiveRepeatingParser;
import org.petitparser.utils.Analyzer;

import java.util.function.Consumer;

/**
 * Identifies {@code .star().flatten()} on character parsers and suggests {@code starString()}.
 */
public class CharacterRepeaterRule extends LinterRule {

  public CharacterRepeaterRule() {
    super(LinterType.WARNING, "Character repeater");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof FlattenParser) {
      Parser repeating = parser.getChildren().getFirst();
      if (repeating instanceof PossessiveRepeatingParser) {
        Parser character = repeating.getChildren().getFirst();
        if (character instanceof CharacterParser) {
          callback.accept(new LinterIssue(
              this,
              parser,
              "A flattened repeater (" + repeating + ") that delegates to a character " +
              "parser (" + character + ") can be much more efficiently implemented " +
              "using `starString`, `plusString`, `timesString`, or " +
              "`repeatString` that directly returns the underlying String " +
              "instead of an intermediate List."
          ));
        }
      }
    }
  }
}
