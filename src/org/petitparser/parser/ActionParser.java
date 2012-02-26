package org.petitparser.parser;

import org.petitparser.Function;
import org.petitparser.ParseContext;
import org.petitparser.Parser;

/**
 * A parser that delegates to another one.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ActionParser<I, O, T> extends DelegateParser<I, O> {

  private final Function<O, T> transform;

  public ActionParser(Parser<I, O> parser, Function<O, T> transform) {
    super(parser);
    this.transform = transform;
  }

  @Override
  public void parse(ParseContext<I, O> context) {
    super.parse(context);
    if (!context.output().isFailure()) {
      context.push(transform.apply(context.pop()));
    }
  }

}
