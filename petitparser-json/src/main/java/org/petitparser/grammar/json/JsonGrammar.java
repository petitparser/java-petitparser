package org.petitparser.grammar.json;

import org.petitparser.tools.GrammarParser;

/**
 * JSON grammar.
 */
public class JsonGrammar extends GrammarParser {
  public JsonGrammar() {
    super(new JsonGrammarDefinition());
  }
}
