package org.petitparser.grammar.xml;

import org.petitparser.grammar.xml.ast.*;

import java.util.Collection;

/**
 * Builds the XML AST.
 */
public class XmlBuilder implements XmlCallback<XmlName, XmlNode, XmlAttribute> {

  @Override
  public XmlNode createAttribute(XmlName name, String text) {
    return new XmlAttribute(name, text);
  }

  @Override
  public XmlNode createComment(String text) {
    return new XmlComment(text);
  }

  @Override
  public XmlNode createCDATA(String text) {
    return new XmlCdata(text);
  }

  @Override
  public XmlNode createDoctype(String text) {
    return new XmlDoctype(text);
  }

  @Override
  public XmlNode createDocument(Collection<XmlNode> children) {
    return new XmlDocument(children);
  }

  @Override
  public XmlNode createElement(
      XmlName name, Collection<XmlAttribute> attributes, Collection<XmlNode> children) {
    return new XmlElement(name, attributes, children);
  }

  @Override
  public XmlNode createProcessing(String target, String text) {
    return new XmlProcessing(target, text);
  }

  @Override
  public XmlName createQualified(String name) {
    return new XmlName(name);
  }

  @Override
  public XmlNode createText(String text) {
    return new XmlText(text);
  }
}
