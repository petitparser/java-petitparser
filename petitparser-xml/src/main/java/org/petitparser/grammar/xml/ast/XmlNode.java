package org.petitparser.grammar.xml.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract XML node.
 */
public abstract class XmlNode implements Iterable<XmlNode> {

  private XmlNode parent;

  /**
   * Answer the parent node of the receiver, or {@code null} if there is none.
   */
  public XmlNode getParent() {
    return parent;
  }

  /**
   * Set the parent of the receiver.
   */
  void setParent(XmlNode parent) {
    this.parent = parent;
  }

  /**
   * Answer the attribute nodes of the receiver.
   */
  public List<XmlAttribute> getAttributes() {
    return Collections.emptyList();
  }

  /**
   * Answer the child nodes of the receiver.
   */
  public List<XmlNode> getChildren() {
    return Collections.emptyList();
  }

  /**
   * Answer an iterator over the receiver, all attributes and nested children.
   */
  @Override
  public Iterator<XmlNode> iterator() {
    List<XmlNode> nodes = new ArrayList<>();
    allAllNodesTo(nodes);
    return nodes.iterator();
  }

  private void allAllNodesTo(List<XmlNode> nodes) {
    nodes.add(this);
    nodes.addAll(getAttributes());
    for (XmlNode node : getChildren()) {
      node.allAllNodesTo(nodes);
    }
  }

  /**
   * Answer the root of the subtree in which this node is found, whether that's
   * a document or another element.
   */
  public XmlNode getRoot() {
    return parent == null ? this : parent.getRoot();
  }

  /**
   * Answer the document that contains this node, or {@code null} if the node is
   * not part of a document.
   */
  public XmlDocument getDocument() {
    return parent == null ? null : parent.getDocument();
  }

  /**
   * Answer the first child of the receiver or {@code null}.
   */
  public final XmlNode getFirstChild() {
    List<XmlNode> children = getChildren();
    return children.isEmpty() ? null : children.get(0);
  }

  /**
   * Answer the last child of the receiver or {@code null}.
   */
  public final XmlNode getLastChild() {
    List<XmlNode> children = getChildren();
    return children.isEmpty() ? null : children.get(children.size() - 1);
  }

  /**
   * Answer the next sibling of the receiver or {@code null}.
   */
  public final XmlNode getNextSibling() {
    XmlNode parent = getParent();
    if (parent == null) {
      return null;
    }
    List<XmlNode> children = parent.getChildren();
    for (int i = 0; i < children.size() - 1; i++) {
      if (children.get(i) == this) {
        return children.get(i + 1);
      }
    }
    return null;
  }

  /**
   * Answer the previous sibling of the receiver or {@code null}.
   */
  public final XmlNode getPreviousSibling() {
    XmlNode parent = getParent();
    if (parent == null) {
      return null;
    }
    List<XmlNode> children = parent.getChildren();
    for (int i = 1; i < children.size(); i++) {
      if (children.get(i) == this) {
        return children.get(i - 1);
      }
    }
    return null;
  }

  /**
   * Answer a print string of the receiver.
   */
  @Override
  public String toString() {
    return super.toString() + " (" + toXmlString() + ")";
  }

  /**
   * Answer an XML string of the receiver.
   */
  public String toXmlString() {
    StringBuilder buffer = new StringBuilder();
    writeTo(buffer);
    return buffer.toString();
  }

  /**
   * Writes the XML string of the receiver to a {@code buffer}.
   */
  public abstract void writeTo(StringBuilder buffer);
}
