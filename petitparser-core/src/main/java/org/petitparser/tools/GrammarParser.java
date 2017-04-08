package org.petitparser.tools;

import org.petitparser.parser.combinators.DelegateParser;

/**
 * A helper to build a parser from a {@link GrammarDefinition}.
 */
public class GrammarParser extends DelegateParser {

  public GrammarParser(GrammarDefinition definition) {
    super(definition.build());
  }

  public GrammarParser(GrammarDefinition definition, String name) {
    super(definition.build(name));
  }
}