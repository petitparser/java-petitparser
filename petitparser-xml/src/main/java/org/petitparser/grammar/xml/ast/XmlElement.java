package org.petitparser.grammar.xml.ast;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * XML element node.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class XmlElement extends XmlParent {

  private final XmlName name;
  private final List<XmlAttribute> attributes;

  public XmlElement(XmlName name, Collection<XmlAttribute> attributes,
      Collection<XmlNode> children) {
    super(children);
    this.name = name;
    this.attributes = ImmutableList.copyOf(attributes);
    for (XmlAttribute attribute : attributes) {
      attribute.setParent(this);
    }
  }

  public XmlName getName() {
    return name;
  }

  @Override
  public List<XmlAttribute> getAttributes() {
    return attributes;
  }

  public String getAttribute(String key) {
    XmlAttribute attribute = getAttributeNode(key);
    return attribute != null ? attribute.getValue() : null;
  }

  public XmlAttribute getAttributeNode(String key) {
    for (XmlAttribute attribute : attributes) {
      if (attribute.getName().getLocal().equals(key)) {
        return attribute;
      }
    }
    return null;
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append('<');
    getName().writeTo(buffer);
    for (XmlAttribute attribute : getAttributes()) {
      buffer.append(' ');
      attribute.writeTo(buffer);
    }
    if (getChildren().isEmpty()) {
      buffer.append(" />");
    } else {
      buffer.append('>');
      super.writeTo(buffer);
      buffer.append("</");
      getName().writeTo(buffer);
      buffer.append('>');
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj) || getClass() != obj.getClass())
      return false;
    XmlElement other = (XmlElement) obj;
    return name.equals(other.name) && attributes.equals(other.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), name, attributes.size());
  }

}
