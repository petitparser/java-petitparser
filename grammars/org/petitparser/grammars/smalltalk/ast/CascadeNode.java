package org.petitparser.grammars.smalltalk.ast;

import java.util.List;

/**
 * AST node for cascaded messages.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class CascadeNode extends ValueNode {

  private List<MessageNode> messages;

  public CascadeNode(List<MessageNode> messages) {
    this.messages = messages;
  }

  public List<MessageNode> getMessages() {
    return messages;
  }

}
