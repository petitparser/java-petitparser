package org.petitparser.grammar.xml;

import org.petitparser.tools.GrammarParser;

/**
 * XmlParser Builder
 */
public class XmlParser extends GrammarParser {

  public XmlParser() {
    super(new XmlDefinition<>(new XmlBuilder()));
  }
}
