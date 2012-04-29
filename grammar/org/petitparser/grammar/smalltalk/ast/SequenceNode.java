package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SequenceNode extends ProgramNode {

  private List<VariableNode> temporaries;
  private List<ProgramNode> statements;

}
