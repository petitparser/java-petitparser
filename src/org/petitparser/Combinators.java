package org.petitparser;

import org.petitparser.parser.ChoiceParser;
import org.petitparser.parser.SequenceParser;

/**
 * Factory for combinators.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Combinators {

  /**
   * Returns an ordered-choice of the {@code parsers}.
   */
  public static ChoiceParser or(Parsable... parsers) {
    return new ChoiceParser(parsers);
  }

  /**
   * Returns a sequence of the {@code parsers}.
   */
  public static SequenceParser seq(Parsable... parsers) {
    return new SequenceParser(parsers);
  }

}
