package org.petitparser.parser;

import java.util.Arrays;
import java.util.List;

import org.petitparser.Parser;


/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class ListParser<I, O> extends AbstractParser<I, O> {

  protected final List<Parser<I, O>> parsers;

  ListParser(Parser<I, O>... parsers) {
    this(Arrays.asList(parsers));
  }

  ListParser(List<Parser<I, O>> parsers) {
    this.parsers = parsers;
  }

}
