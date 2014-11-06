package org.petitparser.grammar.xml.ast;

/**
 * XML doctype node.
 */
public class XmlDoctype extends XmlData {

  public XmlDoctype(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append("<!DOCTYPE").append(getData()).append(">");
  }

}
