package org.petitparser.grammar.smalltalk.ast;

/**
 * An AST node for literal values.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class LiteralValueNode extends LiteralNode {

  private Token token;
  private Object value;

  public Token getToken() {
    return token;
  }

  public void setToken(Token token) {
    this.token = token;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

}
