package org.petitparser.parser;

import java.util.ArrayList;
import java.util.List;

import org.petitparser.Parser;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

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
  public Result<List<T>> parse(Context context) {
    Context current = context;
    List<T> elements = new ArrayList<T>();
    while (elements.size() < min) {
      Result<T> result = parser.parse(current);
      if (result.isFailure()) {
        return result.cast();
      }
      elements.add(result.get());
      current = result;
    }
    while (elements.size() < max) {
      Result<T> result = parser.parse(current);
      if (result.isFailure()) {
        return result.success(elements);
      }
      elements.add(result.get());
      current = result;
    }
    return current.success(elements);
  }

}
