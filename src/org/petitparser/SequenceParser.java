package org.petitparser;

import java.util.ArrayList;
import java.util.List;

import org.petitparser.context.Context;

/**
 * A parser that parses a sequence of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SequenceParser<T> extends AbstractParser<List<T>> {

  private final List<Parser<T>> parsers;

  public SequenceParser(List<Parser<T>> parsers) {
    this.parsers = parsers;
  }

  @Override
  public Context<List<T>> parse(Context<?> context) {
    Context<T> current = context.cast();
    List<T> elements = new ArrayList<T>(parsers.size());
    for (Parser<T> parser : parsers) {
      current = parser.parse(current);
      if (current.isFailure()) {
        return current.cast();
      }
      elements.add(current.get());
    }
    return context.success(elements);
  }

}
