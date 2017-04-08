package org.petitparser.grammar.xml.ast;

import org.petitparser.grammar.xml.XmlDefinition;

/**
 * An XML comment node.
 */
public class XmlComment extends XmlData {

  public XmlComment(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    buffer.append(XmlDefinition.OPEN_COMMENT);
    buffer.append(getData());
    buffer.append(XmlDefinition.CLOSE_COMMENT);
  }
}
