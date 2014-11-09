package org.petitparser.grammar.xml.ast;

import java.util.Collection;

/**
 * XML document node.
 */
public class XmlDocument extends XmlParent {

  public XmlDocument(Collection<XmlNode> children) {
    super(children);
  }

  @Override
  public XmlDocument getDocument() {
    return this;
  }

  public XmlElement getRootElement() {
    for (XmlNode node : getChildren()) {
      if (node instanceof XmlElement) {
        return (XmlElement) node;
      }
    }
    return null;
  }
}
