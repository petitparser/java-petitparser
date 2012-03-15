package org.petitparser.examples.xml.ast;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;

/**
 * Abstract XML node.
 *
 * @author Lukas Renggli (renggli@gmail.com)
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
   * Answer an iterator over the receiver, all attributes and children.
   */
  @Override
  public Iterator<XmlNode> iterator() {
    return Iterators.concat(
        Iterators.singletonIterator(this),
        getAttributes().iterator(),
        getChildren().iterator());
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
    return children.size() > 0 ? children.get(0) : null;
  }

  /**
   * Answer the last child of the receiver or {@code null}.
   */
  public final XmlNode getLastChild() {
    List<XmlNode> children = getChildren();
    return children.size() > 0 ? children.get(children.size() - 1) : null;
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
    int index = children.indexOf(this) + 1;
    return 0 < index && index < children.size() ? children.get(index) : null;
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
    int index = children.indexOf(this) - 1;
    return 0 <= index && index < children.size() ? children.get(index) : null;
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
    StringBuffer buffer = new StringBuffer();
    writeTo(buffer);
    return buffer.toString();
  }

  /**
   * Writes the XML string of the receiver to a {@code buffer}.
   */
  public abstract void writeTo(StringBuffer buffer);

}
