package org.petitparser.grammar.xml.ast;

import org.petitparser.grammar.xml.XmlGrammar;

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

  public XmlElement(XmlName name, Collection<XmlAttribute> attributes,
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
      if (attribute.getName().getLocal().equals(key)) {
        return attribute;
      }
    }
    return null;
  }

  @Override
  public void writeTo(StringBuffer buffer) {
    buffer.append(XmlGrammar.OPEN_ELEMENT);
    getName().writeTo(buffer);
    for (XmlAttribute attribute : getAttributes()) {
      buffer.append(XmlGrammar.WHITESPACE);
      attribute.writeTo(buffer);
    }
    if (getChildren().isEmpty()) {
      buffer.append(XmlGrammar.WHITESPACE);
      buffer.append(XmlGrammar.CLOSE_END_ELEMENT);
    } else {
      buffer.append(XmlGrammar.CLOSE_ELEMENT);
      super.writeTo(buffer);
      buffer.append(XmlGrammar.OPEN_END_ELEMENT);
      getName().writeTo(buffer);
      buffer.append(XmlGrammar.CLOSE_ELEMENT);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    XmlElement other = (XmlElement) obj;
    return name.equals(other.name) && attributes.equals(other.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name, attributes.size());
  }
}
