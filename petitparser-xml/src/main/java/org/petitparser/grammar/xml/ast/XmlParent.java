package org.petitparser.grammar.xml.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Abstract XML node with actual children.
 */
public abstract class XmlParent extends XmlNode {

  private final List<XmlNode> children;

  public XmlParent(Collection<XmlNode> children) {
    this.children = new ArrayList<>(children);
    for (XmlNode child : children) {
      child.setParent(this);
    }
  }

  @Override
  public List<XmlNode> getChildren() {
    return children;
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    for (XmlNode node : getChildren()) {
      node.writeTo(buffer);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    XmlParent other = (XmlParent) obj;
    return Objects.equals(children, other.children);
  }

  @Override
  public int hashCode() {
    return children.size();
  }
}
