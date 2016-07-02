package org.petitparser.grammar.xml.ast;

import org.petitparser.grammar.xml.XmlGrammar;

/**
 * XML text node.
 */
public class XmlCdata extends XmlData {

  public XmlCdata(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    buffer.append(XmlGrammar.OPEN_CDATA);
    buffer.append(getData());
    buffer.append(XmlGrammar.CLOSE_CDATA);
  }
}
