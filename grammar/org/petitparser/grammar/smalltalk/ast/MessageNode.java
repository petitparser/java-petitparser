package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * AST node that represents a message send.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class MessageNode extends ValueNode {

  private ValueNode receiver;
  private List<Token> selectorParts;
  private List<ValueNode> arguments;

  public MessageNode(ValueNode receiver, List<Token> selectorParts, List<ValueNode> arguments) {
    this.receiver = receiver;
    this.selectorParts = selectorParts;
    this.arguments = arguments;
  }

  public ValueNode getReceiver() {
    return receiver;
  }

  public List<Token> getSelectorParts() {
    return selectorParts;
  }

  public List<ValueNode> getArguments() {
    return arguments;
  }

}
