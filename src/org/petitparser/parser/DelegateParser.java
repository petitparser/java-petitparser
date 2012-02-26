package org.petitparser.parser;

import org.petitparser.ParseContext;
import org.petitparser.Parser;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser<I, O> extends AbstractParser<I, O> {

  protected final Parser<I, O> parser;

  DelegateParser(Parser<I, O> parser) {
    this.parser = parser;
  }

  @Override
  public void parse(ParseContext<I, O> context) {
    this.parser.parse(context);
  }

}
