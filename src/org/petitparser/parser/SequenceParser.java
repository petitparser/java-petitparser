package org.petitparser.parser;

import org.petitparser.ParseContext;
import org.petitparser.Parser;

/**
 * A parser that parses a sequence of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SequenceParser<I, O> extends ListParser<I, O> {

  @Override
  public void parse(ParseContext<I, O> context) {
    ParseContext<I, O> snapshot = context.snapshot();
    for (Parser<I, O> parser : this.parsers) {
      parser.parse(context);
      if (context.isFailure()) {
        context.restore(snapshot);
        return;
      }
    }
  }

}
