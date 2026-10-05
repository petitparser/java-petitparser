package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.actions.FlattenParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.NewlineParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.parser.repeating.RepeatingCharacterParser;
import org.petitparser.utils.Analyzer;

import java.util.function.Consumer;

/**
 * Identifies {@code flatten()} on parsers already producing Strings.
 */
public class UnnecessaryFlattenRule extends LinterRule {

  public UnnecessaryFlattenRule() {
    super(LinterType.WARNING, "Unnecessary flatten");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof FlattenParser && ((FlattenParser) parser).getMessage() == null) {
      Parser delegate = parser.getChildren().get(0);
      if (delegate instanceof CharacterParser ||
          delegate instanceof FlattenParser ||
          delegate instanceof NewlineParser ||
          delegate instanceof StringParser ||
          delegate instanceof RepeatingCharacterParser) {
        callback.accept(new LinterIssue(
            this,
            parser,
            "A flatten parser delegating to a parser (" + delegate + ") that is " +
            "returning the accepted input string adds unnecessary overhead and can be removed."
        ));
      }
    }
  }
}
