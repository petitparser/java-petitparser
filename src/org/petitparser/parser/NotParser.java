package org.petitparser.parser;

import org.petitparser.ParseContext;
import org.petitparser.Parser;

/**
 * The not-predicate, a parser that succeeds whenever its delegate does not, but
 * consumes no input [Parr 1994, 1995].
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class NotParser<I, O> extends DelegateParser<I, O> {

  NotParser(Parser<I, O> parser) {
    super(parser);
  }

  @Override
  public void parse(ParseContext<I, O> context) {
    ParseContext<I, O> snapshot = context.snapshot();
    super.parse(context);
    if (context.isFailure()) {
      context.restore(snapshot);
      context.push(null);
    } else {
      // TOOD(renggli): push error
      context.push(null);
    }
  }

}
