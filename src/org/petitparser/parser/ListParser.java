package org.petitparser.parser;

/**
 * Abstract parser that parses a list of things in some way (to be specified by
 * the subclasses).
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class ListParser extends Parser {

  protected final Parser[] parsers;

  public ListParser(Parser... parsers) {
    this.parsers = parsers;
  }

}
