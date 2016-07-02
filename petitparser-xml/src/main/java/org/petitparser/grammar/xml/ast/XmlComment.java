package org.petitparser.grammar.xml.ast;

import org.petitparser.grammar.xml.XmlGrammar;

/**
 * An XML comment node.
 */
public class XmlComment extends XmlData {

  public XmlComment(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    buffer.append(XmlGrammar.OPEN_COMMENT);
    buffer.append(getData());
    buffer.append(XmlGrammar.CLOSE_COMMENT);
  }
}
