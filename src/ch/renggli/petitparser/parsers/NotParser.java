package ch.renggli.petitparser.parsers;

import ch.renggli.petitparser.ParseContext;
import ch.renggli.petitparser.Parser;

/**
 * The not-predicate, a parser that succeeds whenever its delegate does not, but
 * consumes no input [Parr 1994, 1995].
 *
 * @author renggli@gmail.com (Lukas Renggli)
 */
public class NotParser<T> extends DelegateParser<T> {

  NotParser(Parser<T> parser) {
    super(parser);
  }

  @Override
  public void parseOn(ParseContext context) {
    int position = context.input().getPosition();
    super.parseOn(context);
    context.input().setPosition(position);
    if (context.output().isError()) {
      context.output().pop();
      context.output().push(new ParseResult<T>());
    } else {
      context.output().push()
    }
  }

}
