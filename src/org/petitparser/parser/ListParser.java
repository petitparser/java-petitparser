package org.petitparser.parser;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract parser that parses a list of things in some way (to be specified by
 * the subclasses).
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class ListParser extends Parser {

  protected Parser[] parsers;

  public ListParser(Parser... parsers) {
    this.parsers = parsers;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    ListParser clone = (ListParser) super.clone();
    clone.parsers = parsers.clone();
    return clone;
  }

  @Override
  public void replace(Parser source, Parser target) {
    super.replace(source, target);
    for (int i = 0; i < parsers.length; i++) {
      if (parsers[i] == source) {
        parsers[i] = target;
      }
    }
  }

  @Override
  public List<Parser> getChildren() {
    List<Parser> children = super.getChildren();
    children.addAll(Arrays.asList(parsers));
    return children;
  }

}
