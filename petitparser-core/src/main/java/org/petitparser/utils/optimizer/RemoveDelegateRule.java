package org.petitparser.utils.optimizer;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.combinators.SettableParser;

import java.util.HashSet;
import java.util.Set;

/**
 * Removes unnecessary delegate parsers (such as direct {@link DelegateParser} or {@link SettableParser})
 * that only forward to an inner parser without adding behavior.
 */
public class RemoveDelegateRule implements OptimizeRule {

  @Override
  public Parser apply(Parser parser) {
    if (parser == null) {
      return null;
    }
    Set<Parser> seen = null;
    while ((DelegateParser.class.equals(parser.getClass()) ||
        SettableParser.class.equals(parser.getClass())) &&
        !parser.getChildren().isEmpty()) {
      if (seen == null) {
        seen = new HashSet<>();
      }
      if (!seen.add(parser)) {
        break;
      }
      parser = parser.getChildren().get(0);
    }
    return parser;
  }
}
