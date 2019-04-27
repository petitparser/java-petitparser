package org.petitparser.grammar.xml.ast;

import org.petitparser.grammar.xml.XmlDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * XML element node.
 */
public class XmlElement extends XmlParent {

  private final XmlName name;
  private final List<XmlAttribute> attributes;

  public XmlElement(
      XmlName name, Collection<XmlAttribute> attributes,
      Collection<XmlNode> children) {
    super(children);
    this.name = name;
    this.attributes = new ArrayList<>(attributes);
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
      if (Objects.equals(attribute.getName().getLocal(), key)) {
        return attribute;
      }
    }
    return null;
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    buffer.append(XmlDefinition.OPEN_ELEMENT);
    getName().writeTo(buffer);
    for (XmlAttribute attribute : getAttributes()) {
      buffer.append(XmlDefinition.WHITESPACE);
      attribute.writeTo(buffer);
    }
    if (getChildren().isEmpty()) {
      buffer.append(XmlDefinition.WHITESPACE);
      buffer.append(XmlDefinition.CLOSE_END_ELEMENT);
    } else {
      buffer.append(XmlDefinition.CLOSE_ELEMENT);
      super.writeTo(buffer);
      buffer.append(XmlDefinition.OPEN_END_ELEMENT);
      getName().writeTo(buffer);
      buffer.append(XmlDefinition.CLOSE_ELEMENT);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    XmlElement other = (XmlElement) obj;
    return Objects.equals(name, other.name) &&
        Objects.equals(attributes, other.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name, attributes.size());
  }
}
