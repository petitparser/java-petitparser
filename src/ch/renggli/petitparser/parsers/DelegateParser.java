package ch.renggli.petitparser.parsers;

import ch.renggli.petitparser.ParseContext;
import ch.renggli.petitparser.Parser;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser<I, S, O> extends AbstractParser<I, S, O> {

  private final Parser<I, S, O> parser;

  DelegateParser(Parser<I, S, O> parser) {
    this.parser = parser;
  }

  @Override
  public void parseOn(ParseContext context) {
    this.parser.parseOn(context);
  }

}
