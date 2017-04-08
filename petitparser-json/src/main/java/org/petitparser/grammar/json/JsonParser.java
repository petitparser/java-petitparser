package org.petitparser.grammar.json;

import org.petitparser.tools.GrammarParser;

/**
 * JSON parser.
 */
public class JsonParser extends GrammarParser {
  public JsonParser() {
    super(new JsonParserDefinition());
  }
}
