package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * An AST node for pragmas.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class PragmaNode extends ProgramNode {

  private String selector;
  private List<Token> selectorParts;
  private List<LiteralNode> arguments;

  public List<LiteralNode> getArguments() {
    return arguments;
  }

  public void setArguments(List<LiteralNode> arguments) {
    for (LiteralNode argument : arguments) {
      argument.setParent(this);
    }
    this.arguments = arguments;
  }

}
