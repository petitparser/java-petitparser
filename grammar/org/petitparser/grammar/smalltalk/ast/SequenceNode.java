package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * AST node for sequences of statements.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SequenceNode extends ProgramNode {

  private List<VariableNode> temporaries;
  private List<ProgramNode> statements;

  public List<VariableNode> getTemporaries() {
    return temporaries;
  }

  public void setTemporaries(List<VariableNode> temporaries) {
    for (VariableNode temporary : temporaries) {
      temporary.setParent(this);
    }
    this.temporaries = temporaries;
  }

  public List<ProgramNode> getStatements() {
    return statements;
  }

  public void setStatements(List<ProgramNode> statements) {
    for (ProgramNode statement : statements) {
      statement.setParent(this);
    }
    this.statements = statements;
  }

}
