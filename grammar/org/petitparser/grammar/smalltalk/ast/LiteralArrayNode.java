package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * An AST node for literal arrays.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class LiteralArrayNode extends LiteralNode {

  private List<LiteralNode> literals;

  public List<LiteralNode> getContents() {
    return literals;
  }

  public void setContents(List<LiteralNode> literals) {
    for (LiteralNode literal : literals) {
      literal.setParent(this);
    }
    this.literals = literals;
  }

}
