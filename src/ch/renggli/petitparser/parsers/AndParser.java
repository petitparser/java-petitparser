package ch.renggli.petitparser.parsers;

import ch.renggli.petitparser.ParseContext;
import ch.renggli.petitparser.Parser;

/**
 * The and-predicate, a parser that succeeds whenever its delegate does, but
 * does not consume the input stream [Parr 1994, 1995].
 *
 * @author renggli@gmail.com (Lukas Renggli)
 */
public class AndParser<T> extends DelegateParser<T> {

  AndParser(Parser<T> parser) {
    super(parser);
  }

  @Override
  public void parseOn(ParseContext context) {
    int position = context.input().getPosition();
    super.parseOn(context);
    context.input().setPosition(position);
  }

}
