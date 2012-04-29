package org.petitparser.grammar.smalltalk.ast;

/**
 * Abstract class that represents an abstract syntax tree node in a Smalltalk
 * program.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class ProgramNode {

  private ProgramNode parent;

  public ProgramNode getParent() {
    return parent;
  }

  public void setParent(ProgramNode parent) {
    this.parent = parent;
  }

}
