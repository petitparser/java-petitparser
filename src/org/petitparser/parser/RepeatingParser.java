package org.petitparser.parser;

import java.util.ArrayList;
import java.util.List;

import org.petitparser.Parser;
import org.petitparser.context.Context;

/**
 * A parser that parses a sequence of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class RepeatingParser<T> extends AbstractParser<List<T>> {

  private final Parser<T> parser;
  private final int min, max;

  public RepeatingParser(Parser<T> parser, int min, int max) {
    this.parser = parser;
    this.min = min;
    this.max = max;
  }

  @Override
  public Context<List<T>> parse(Context<?> context) {
    Context<T> current = context.cast();
    List<T> elements = new ArrayList<T>();
    while (elements.size() < min) {
      current = parser.parse(current);
      if (current.isFailure()) {
        return current.cast();
      }
      elements.add(current.get());
    }
    while (elements.size() < max) {
      current = parser.parse(current);
      if (current.isFailure()) {
        return current.success(elements);
      }
      elements.add(current.get());
    }
    return current.success(elements);
  }

}
