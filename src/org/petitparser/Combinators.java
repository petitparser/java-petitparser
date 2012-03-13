package org.petitparser;

import org.petitparser.parser.ChoiceParser;
import org.petitparser.parser.Parser;
import org.petitparser.parser.SequenceParser;

/**
 * Factory for combinators.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Combinators {

  /**
   * Returns an ordered-choice of the provided {@code parsers}.
   */
  public static Parser or(Parser... parsers) {
    return parsers.length == 1 ? parsers[0] : new ChoiceParser(parsers);
  }

  /**
   * Returns an ordered-sequence of the provided {@code parsers}.
   */
  public static Parser seq(Parser... parsers) {
    return new SequenceParser(parsers);
  }

}
