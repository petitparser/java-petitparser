package org.petitparser.grammars.smalltalk.ast;

import java.util.List;

/**
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class BlockNode extends ValueNode {

  private List<VariableNode> arguments;
  private SequenceNode body;

}
