package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * An AST node for methods.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class MethodNode extends ProgramNode {

  private List<Token> selectorParts;
  private List<VariableNode> arguments;
  private List<PragmaNode> pragmas;
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

  public List<PragmaNode> getPragmas() {
    return pragmas;
  }

  public void setPragmas(List<PragmaNode> pragmas) {
    for (PragmaNode pragma : pragmas) {
      pragma.setParent(this);
    }
    this.pragmas = pragmas;
  }

  public SequenceNode getBody() {
    return body;
  }

  public void setBody(SequenceNode body) {
    body.setParent(this);
    this.body = body;
  }

}
