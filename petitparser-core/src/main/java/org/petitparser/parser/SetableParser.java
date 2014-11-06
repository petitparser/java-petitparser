package org.petitparser.parser;

/**
 * A parser that can be set to point to another parser.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SetableParser extends DelegateParser {

  public SetableParser(Parser delegate) {
    super(delegate);
  }

  public void set(Parser parser) {
    replace(getChildren().get(0), parser);
  }

}
