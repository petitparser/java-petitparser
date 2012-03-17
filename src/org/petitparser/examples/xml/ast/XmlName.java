package org.petitparser.examples.xml.ast;

import com.google.common.base.Objects;

/**
 * XML entity name.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class XmlName implements Cloneable {

  private final String prefix;
  private final String local;
  private final String uri;

  public XmlName(String name) {
    this(name, null);
  }

  public XmlName(String name, String url) {
    int index = name.indexOf(':');
    if (index < 0) {
      this.prefix = null;
      this.local = name;
    } else {
      this.prefix = name.substring(0, index);
      this.local = name.substring(index + 1, name.length());
    }
    this.uri = url;
  }

  public String getLocal() {
    return local;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getUri() {
    return uri;
  }

  public String getQualified() {
    return toXmlString();
  }

  @Override
  public String toString() {
    return super.toString() + " (" + toXmlString() + ")";
  }

  public String toXmlString() {
    StringBuffer buffer = new StringBuffer();
    writeTo(buffer);
    return buffer.toString();
  }

  public void writeTo(StringBuffer buffer) {
    if (prefix != null) {
      buffer.append(prefix).append(':');
    }
    buffer.append(local);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    XmlName other = (XmlName) obj;
    return Objects.equal(prefix, other.prefix) && local.equals(other.local);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(prefix, local);
  }

}
