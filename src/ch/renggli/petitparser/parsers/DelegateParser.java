package ch.renggli.petitparser.parsers;

import ch.renggli.petitparser.ParseContext;
import ch.renggli.petitparser.Parser;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class DelegateParser<T> implements Parser<T> {

  private final Parser<T> parser;

  DelegateParser(Parser<T> parser) {
    this.parser = parser;
  }

  @Override
  public void parseOn(ParseContext context) {
    this.parser.parseOn(context);
  }

}
