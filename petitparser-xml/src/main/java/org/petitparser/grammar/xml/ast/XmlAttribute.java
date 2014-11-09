package org.petitparser.grammar.xml.ast;

import java.util.Objects;

/**
 * XML attribute node.
 */
public class XmlAttribute extends XmlNode {

  private final XmlName name;
  private final String value;

  public XmlAttribute(XmlName name, String value) {
    this.name = name;
    this.value = value;
  }

  public XmlName getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    name.writeTo(buffer);
    buffer.append("=\"").append(value).append("\"");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    XmlAttribute other = (XmlAttribute) obj;
    return name.equals(other.name) && value.equals(other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }
}
