package org.petitparser.grammars.xml;

import java.util.Collection;
import java.util.List;

import org.petitparser.grammars.xml.ast.XmlAttribute;
import org.petitparser.grammars.xml.ast.XmlComment;
import org.petitparser.grammars.xml.ast.XmlDoctype;
import org.petitparser.grammars.xml.ast.XmlDocument;
import org.petitparser.grammars.xml.ast.XmlElement;
import org.petitparser.grammars.xml.ast.XmlName;
import org.petitparser.grammars.xml.ast.XmlNode;
import org.petitparser.grammars.xml.ast.XmlProcessing;
import org.petitparser.grammars.xml.ast.XmlText;
import org.petitparser.parser.Parser;

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
  Parser attribute() {
    return super.attribute().map(new Function<List<?>, XmlAttribute>() {
      @Override
      public XmlAttribute apply(List<?> argument) {
        return new XmlAttribute((XmlName) argument.get(0),
            (String) argument.get(1));
      }
    });
  }

  @Override
  Parser comment() {
    return super.comment().map(new Function<String, XmlComment>() {
      @Override
      public XmlComment apply(String string) {
        return new XmlComment(string);
      }
    });
  }

  @Override
  Parser doctype() {
    return super.doctype().map(new Function<String, XmlDoctype>() {
      @Override
      public XmlDoctype apply(String string) {
        return new XmlDoctype(string);
      }
    });
  }

  @Override
  Parser document() {
    return super.document().map(new Function<List<XmlNode>, XmlDocument>() {
      @Override
      public XmlDocument apply(List<XmlNode> nodes) {
        return new XmlDocument(Collections2.filter(nodes, Predicates.notNull()));
      }
    });
  }

  @Override
  Parser element() {
    return super.element().map(new Function<List<?>, XmlElement>() {
      @Override
      @SuppressWarnings("unchecked")
      public XmlElement apply(List<?> list) {
        return new XmlElement((XmlName) list.get(0),
            (Collection<XmlAttribute>) list.get(1),
            (Collection<XmlNode>) list.get(2));
      }
    });
  }

  @Override
  Parser processing() {
    return super.processing().map(new Function<List<String>, XmlProcessing>() {
      @Override
      public XmlProcessing apply(List<String> list) {
        return new XmlProcessing(list.get(0), list.get(1));
      }
    });
  }

  @Override
  Parser qualified() {
    return super.qualified().map(new Function<String, XmlName>() {
      @Override
      public XmlName apply(String name) {
        return new XmlName(name);
      }
    });
  }

  @Override
  Parser characterData() {
    return super.characterData().map(new Function<String, XmlText>() {
      @Override
      public XmlText apply(String data) {
        return new XmlText(data);
      }
    });
  }

}
