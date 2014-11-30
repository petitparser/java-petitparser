package org.petitparser.grammar.xml.ast;

/**
 * XML text node.
 */
public class XmlCdata extends XmlData {

  public XmlCdata(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append("<![CDATA[").append(getData()).append("]]>");
  }
}
