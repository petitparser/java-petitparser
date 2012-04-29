package org.petitparser.grammars.xml.ast;

/**
 * XML text node.
 *
 * @author Lukas Renggli (renggli@gmail.com)
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
