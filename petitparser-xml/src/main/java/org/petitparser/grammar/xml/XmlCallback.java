package org.petitparser.grammar.xml;

import java.util.Collection;

/**
 * Interface with callbacks form the {@link XmlDefinition}.
 */
public interface XmlCallback<TName, TNode, TAttribute> {

  TNode createAttribute(TName name, String text);

  TNode createComment(String text);

  TNode createCDATA(String text);

  TNode createDoctype(String text);

  TNode createDocument(Collection<TNode> children);

  TNode createElement(
      TName name, Collection<TAttribute> attributes,
      Collection<TNode> children);

  TNode createProcessing(String target, String text);

  TName createQualified(String name);

  TNode createText(String text);
}
