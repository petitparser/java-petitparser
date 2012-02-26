package org.petitparser.parser;

import org.petitparser.ParseContext;
import org.petitparser.Parser;

/**
 * A parser that uses the first parser that succeeds.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ChoiceParser<I, O> extends ListParser<I, O> {

  @Override
  public void parse(ParseContext<I, O> context) {
    for (Parser<I, O> parser : this.parsers) {
      parser.parse(context);
      if (context.isFailure()) {
        context.pop();
      } else {
        return;
      }
    }
  }

}
