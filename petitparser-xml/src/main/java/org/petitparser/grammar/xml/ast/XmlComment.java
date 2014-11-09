package org.petitparser.grammar.xml.ast;

/**
 * An XML comment node.
 */
public class XmlComment extends XmlData {

  public XmlComment(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append("<!--").append(getData()).append("-->");
  }
}
