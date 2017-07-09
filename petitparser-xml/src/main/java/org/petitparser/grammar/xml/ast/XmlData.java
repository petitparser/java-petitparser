package org.petitparser.grammar.xml.ast;

import java.util.Objects;

/**
 * Abstract XML data node.
 */
public abstract class XmlData extends XmlNode {

  private final String data;

  public XmlData(String data) {
    this.data = data;
  }

  public String getData() {
    return data;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    XmlData other = (XmlData) obj;
    return Objects.equals(data, other.data);
  }

  @Override
  public int hashCode() {
    return data.hashCode();
  }
}
