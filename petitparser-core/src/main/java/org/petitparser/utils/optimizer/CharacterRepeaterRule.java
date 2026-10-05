package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;
import org.petitparser.parser.actions.FlattenParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.repeating.PossessiveRepeatingParser;
import org.petitparser.parser.repeating.RepeatingCharacterParser;

/**
 * Transforms {@code FlattenParser(PossessiveRepeatingParser(CharacterParser))} into an optimized
 * {@link RepeatingCharacterParser}.
 */
public class CharacterRepeaterRule implements OptimizeRule {

  @Override
  public Parser apply(Parser parser) {
    if (parser instanceof FlattenParser) {
      FlattenParser flatten = (FlattenParser) parser;
      if (!flatten.getChildren().isEmpty()) {
        Parser delegate = flatten.getChildren().get(0);
        if (delegate instanceof PossessiveRepeatingParser) {
          PossessiveRepeatingParser repeating = (PossessiveRepeatingParser) delegate;
          if (!repeating.getChildren().isEmpty()) {
            Parser repeatingDelegate = repeating.getChildren().get(0);
            if (repeatingDelegate instanceof CharacterParser) {
              CharacterParser character = (CharacterParser) repeatingDelegate;
              return character.repeatString(
                  repeating.getMin(), repeating.getMax(), flatten.getMessage());
            }
          }
        }
      }
    }
    return parser;
  }
}
