package org.petitparser.grammar.xml.ast;

import java.util.Objects;

/**
 * XML entity name.
 */
public class XmlName implements Cloneable {

  private final String prefix;
  private final String local;

  public XmlName(String name) {
    int index = name.indexOf(':');
    if (index < 0) {
      this.prefix = null;
      this.local = name;
    } else {
      this.prefix = name.substring(0, index);
      this.local = name.substring(index + 1, name.length());
    }
  }

  public String getLocal() {
    return local;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getQualified() {
    return toXmlString();
  }

  @Override
  public String toString() {
    return super.toString() + " (" + toXmlString() + ")";
  }

  public String toXmlString() {
    StringBuilder buffer = new StringBuilder();
    writeTo(buffer);
    return buffer.toString();
  }

  public void writeTo(StringBuilder buffer) {
    if (prefix != null) {
      buffer.append(prefix).append(':');
    }
    buffer.append(local);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    XmlName other = (XmlName) obj;
    return Objects.equals(prefix, other.prefix) &&
        Objects.equals(local, other.local);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prefix, local);
  }
}
