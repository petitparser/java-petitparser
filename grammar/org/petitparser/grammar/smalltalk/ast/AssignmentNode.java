package org.petitparser.grammar.smalltalk.ast;

/**
 * AST node for assignments.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class AssignmentNode extends ValueNode {

  private VariableNode variable;
  private ValueNode value;

  public VariableNode getVariable() {
    return variable;
  }

  public void setVariable(VariableNode variable) {
    variable.setParent(this);
    this.variable = variable;
  }

  public ValueNode getValue() {
    return value;
  }

  public void setValue(ValueNode value) {
    value.setParent(this);
    this.value = value;
  }

}
