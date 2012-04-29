package org.petitparser.grammars.smalltalk.ast;

/**
 * AST node that represent a variable.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class VariableNode extends ValueNode {

  private Token token;

  public VariableNode(Token token) {
    this.token = token;
  }

  public Token getToken() {
    return token;
  }

}
