package org.petitparser.grammar.xml.ast;

import org.petitparser.grammar.xml.XmlCharacterParser;

/**
 * XML text node.
 */
public class XmlText extends XmlData {

  public XmlText(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append(XmlCharacterParser.encodeXmlText(getData()));
  }
}
