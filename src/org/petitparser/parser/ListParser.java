package org.petitparser.parser;

import org.petitparser.Parsable;

/**
 * Abstract parser that parses a list of things in some way (to be specified by
 * the subclasses).
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class ListParser extends Parser {

  protected final Parsable[] parsers;

  public ListParser(Parsable... parsers) {
    this.parsers = parsers;
  }

}
