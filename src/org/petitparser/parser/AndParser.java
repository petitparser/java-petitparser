package org.petitparser.parser;

import org.petitparser.ParseContext;
import org.petitparser.Parser;

/**
 * The and-predicate, a parser that succeeds whenever its delegate does, but
 * does not consume the input stream [Parr 1994, 1995].
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class AndParser<I, O> extends DelegateParser<I, O> {

  AndParser(Parser<I, O> parser) {
    super(parser);
  }

  @Override
  public void parse(ParseContext<I, O> context) {
    ParseContext<I, O> snapshot = context.snapshot();
    super.parse(context);
    O element = context.pop();
    context.restore(snapshot);
    context.push(element);
  }

}
