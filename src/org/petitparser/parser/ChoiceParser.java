package org.petitparser.parser;

import java.util.List;

import org.petitparser.Parser;
import org.petitparser.context.Context;

/**
 * A parser that uses the first parser that succeeds.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ChoiceParser<T> extends AbstractParser<T> {

  private final List<Parser<? extends T>> parsers;

  public ChoiceParser(List<Parser<? extends T>> parsers) {
    this.parsers = parsers;
  }

  @Override
  public Context<T> parse(Context<?> context) {
    Context<?> current = context;
    for (Parser<?> parser : parsers) {
      current = parser.parse(context);
      if (current.isSuccess()) {
        return current.cast();
      }
    }
    return current.cast();
  }

}
