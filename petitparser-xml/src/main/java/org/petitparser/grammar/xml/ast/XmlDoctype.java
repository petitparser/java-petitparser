package org.petitparser.grammar.xml.ast;

import org.petitparser.grammar.xml.XmlGrammar;

/**
 * XML doctype node.
 */
public class XmlDoctype extends XmlData {

  public XmlDoctype(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append(XmlGrammar.OPEN_DOCTYPE);
    buffer.append(XmlGrammar.WHITESPACE);
    buffer.append(getData());
    buffer.append(XmlGrammar.CLOSE_DOCTYPE);
  }
}
