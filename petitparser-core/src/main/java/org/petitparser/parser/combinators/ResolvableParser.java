package org.petitparser.parser.combinators;

import org.petitparser.parser.Parser;

/**
 * Interface of a parser that can be resolved to another one.
 */
public interface ResolvableParser {

  /**
   * Resolves this parser to another one.
   *
   * @return the resolved parser
   */
  Parser resolve();
}
