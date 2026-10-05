package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;
import org.petitparser.parser.actions.FlattenParser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.repeating.PossessiveRepeatingParser;
import org.petitparser.parser.repeating.RepeatingCharacterParser;

import java.util.HashSet;
import java.util.Set;

/**
 * Transforms {@code FlattenParser(PossessiveRepeatingParser(CharacterParser))} into an optimized
 * {@link RepeatingCharacterParser}.
 */
public class CharacterRepeaterRule implements OptimizeRule {

  @Override
  public Parser apply(Parser parser) {
    if (parser != null && FlattenParser.class.equals(parser.getClass())) {
      FlattenParser flatten = (FlattenParser) parser;
      if (!flatten.getChildren().isEmpty()) {
        Parser delegate = unwrap(flatten.getChildren().get(0));
        if (delegate != null && PossessiveRepeatingParser.class.equals(delegate.getClass())) {
          PossessiveRepeatingParser repeating = (PossessiveRepeatingParser) delegate;
          if (!repeating.getChildren().isEmpty()) {
            Parser repeatingDelegate = unwrap(repeating.getChildren().get(0));
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

  private Parser unwrap(Parser parser) {
    Set<Parser> seen = null;
    while (parser != null &&
        (DelegateParser.class.equals(parser.getClass()) ||
         SettableParser.class.equals(parser.getClass())) &&
        !parser.getChildren().isEmpty()) {
      if (seen == null) {
        seen = new HashSet<>();
      }
      if (!seen.add(parser)) {
        break;
      }
      Parser next = parser.getChildren().get(0);
      if (next == null) {
        break;
      }
      parser = next;
    }
    return parser;
  }
}
