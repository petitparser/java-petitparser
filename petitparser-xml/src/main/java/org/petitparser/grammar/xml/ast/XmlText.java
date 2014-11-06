package org.petitparser.grammar.xml.ast;

/**
 * XML text node.
 */
public class XmlText extends XmlData {

  public XmlText(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append(getData());
  }

}
