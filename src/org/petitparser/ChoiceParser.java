package org.petitparser;

import java.util.List;

import org.petitparser.context.Context;

/**
 * A parser that uses the first parser that succeeds.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ChoiceParser<T> extends AbstractParser<T> {

  private final List<Parser<T>> parsers;

  public ChoiceParser(List<Parser<T>> parsers) {
    this.parsers = parsers;
  }

  @Override
  public Context<T> parse(Context<?> context) {
    Context<T> current = context.cast();
    for (Parser<T> parser : parsers) {
      current = parser.parse(context);
      if (current.isSuccess()) {
        return current;
      }
    }
    return current;
  }

}
