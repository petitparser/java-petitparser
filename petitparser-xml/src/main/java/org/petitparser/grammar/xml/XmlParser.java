package org.petitparser.grammar.xml;

import org.petitparser.grammar.xml.ast.XmlAttribute;
import org.petitparser.grammar.xml.ast.XmlCdata;
import org.petitparser.grammar.xml.ast.XmlDoctype;
import org.petitparser.grammar.xml.ast.XmlDocument;
import org.petitparser.grammar.xml.ast.XmlElement;
import org.petitparser.grammar.xml.ast.XmlName;
import org.petitparser.grammar.xml.ast.XmlNode;
import org.petitparser.grammar.xml.ast.XmlProcessing;
import org.petitparser.grammar.xml.ast.XmlText;

import java.util.Collection;
import java.util.List;

/**
 * XML parser definition.
 */
public class XmlParser extends XmlGrammar<XmlNode, XmlName> {

  @Override
  protected XmlNode createAttribute(XmlName name, String text) {
    return new XmlAttribute(name, text);
  }

  @Override
  protected XmlNode createComment(String text) {
    return new XmlText(text);
  }

  @Override
  protected XmlNode createCDATA(String text) {
    return new XmlCdata(text);
  }

  @Override
  protected XmlNode createDoctype(String text) {
    return new XmlDoctype(text);
  }

  @Override
  protected XmlNode createDocument(Collection<XmlNode> children) {
    return new XmlDocument(children);
  }

  @Override
  protected XmlNode createElement(XmlName name, Collection<XmlNode> attributes, Collection<XmlNode> children) {
    return new XmlElement(name, (List<XmlAttribute>) (List<?>) attributes, children);
  }

  @Override
  protected XmlNode createProcessing(String target, String text) {
    return new XmlProcessing(target, text);
  }

  @Override
  protected XmlName createQualified(String name) {
    return new XmlName(name);
  }

  @Override
  protected XmlNode createText(String text) {
    return new XmlText(text);
  }
}
