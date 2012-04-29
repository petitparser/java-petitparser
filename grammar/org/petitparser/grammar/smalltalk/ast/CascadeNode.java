package org.petitparser.grammar.smalltalk.ast;

import java.util.List;

/**
 * AST node for cascaded messages.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class CascadeNode extends ValueNode {

  private List<MessageNode> messages;

  public List<MessageNode> getMessages() {
    return messages;
  }

  public void setMessages(List<MessageNode> messages) {
    for (MessageNode message : messages) {
      message.setParent(this);
    }
    this.messages = messages;
  }

}
