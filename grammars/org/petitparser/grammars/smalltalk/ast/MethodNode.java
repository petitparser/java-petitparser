package org.petitparser.grammars.smalltalk.ast;

import java.util.List;

/**
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class MethodNode extends ProgramNode {

  private List<Token> selectorParts;
  private List<VariableNode> arguments;
  private List<PragmaNode> pragmas;
  private SequenceNode body;

}
