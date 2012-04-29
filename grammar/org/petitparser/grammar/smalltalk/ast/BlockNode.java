package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * AST node for block closures.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class BlockNode extends ValueNode {

  private List<VariableNode> arguments;
  private SequenceNode body;

  public List<VariableNode> getArguments() {
    return arguments;
  }

  public void setArguments(List<VariableNode> arguments) {
    for (VariableNode argument : arguments) {
      argument.setParent(this);
    }
    this.arguments = arguments;
  }

  public SequenceNode getBody() {
    return body;
  }

  public void setBody(SequenceNode body) {
    body.setParent(this);
    this.body = body;
  }

}
