package org.petitparser.grammar.xml;

import java.util.Collection;
import java.util.List;

import org.petitparser.grammar.xml.ast.XmlAttribute;
import org.petitparser.grammar.xml.ast.XmlComment;
import org.petitparser.grammar.xml.ast.XmlDoctype;
import org.petitparser.grammar.xml.ast.XmlDocument;
import org.petitparser.grammar.xml.ast.XmlElement;
import org.petitparser.grammar.xml.ast.XmlName;
import org.petitparser.grammar.xml.ast.XmlNode;
import org.petitparser.grammar.xml.ast.XmlProcessing;
import org.petitparser.grammar.xml.ast.XmlText;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * XML parser definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class XmlParser extends XmlGrammar {

  @Override
  protected void initialize() {
    super.initialize();
    action("attribute", new Function<List<?>, XmlAttribute>() {
      @Override
      public XmlAttribute apply(List<?> argument) {
        return new XmlAttribute((XmlName) argument.get(0),
            (String) argument.get(1));
      }
    });
    action("comment", XmlComment::new);
    action("doctype", XmlDoctype::new);
    action("document", new Function<List<XmlNode>, XmlDocument>() {
      @Override
      public XmlDocument apply(List<XmlNode> nodes) {
        return new XmlDocument(Collections2.filter(nodes, Predicates.notNull()));
      }
    });
    action("element", new Function<List<?>, XmlElement>() {
      @Override
      @SuppressWarnings("unchecked")
      public XmlElement apply(List<?> list) {
        return new XmlElement((XmlName) list.get(0),
            (Collection<XmlAttribute>) list.get(1),
            (Collection<XmlNode>) list.get(2));
      }
    });
    action("processing", new Function<List<String>, XmlProcessing>() {
      @Override
      public XmlProcessing apply(List<String> list) {
        return new XmlProcessing(list.get(0), list.get(1));
      }
    });
    action("qualified", XmlName::new);
    action("characterData", XmlText::new);
  }

}
