package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class PragmaNode extends ProgramNode {

  private String selector;
  private List<Token> selectorParts;
  private List<LiteralNode> arguments;

}
