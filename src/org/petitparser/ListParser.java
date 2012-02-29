package org.petitparser;

import java.util.List;


/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class ListParser<T> extends AbstractParser<T> {

  final List<Parser<T>> parsers;

  ListParser(List<Parser<T>> parsers) {
    this.parsers = parsers;
  }

}
