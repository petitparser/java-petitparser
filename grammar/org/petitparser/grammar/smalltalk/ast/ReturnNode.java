package org.petitparser.grammar.smalltalk.ast;

/**
 * AST node for return nodes.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ReturnNode extends ProgramNode {

  private ValueNode value;

  public ValueNode getValue() {
    return value;
  }

  public void setValue(ValueNode value) {
    value.setParent(this);
    this.value = value;
  }

}
