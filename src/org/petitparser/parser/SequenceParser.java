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
public class SequenceParser<T> extends AbstractParser<List<T>> {

  private final List<Parser<T>> parsers;

  public SequenceParser(List<Parser<T>> parsers) {
    this.parsers = parsers;
  }

  @Override
  public Result<List<T>> parse(Context context) {
    Context current = context;
    List<T> elements = new ArrayList<T>(parsers.size());
    for (Parser<T> parser : parsers) {
      Result<T> result = parser.parse(current);
      if (result.isFailure()) {
        return result.cast();
      }
      elements.add(result.get());
      current = result;
    }
    return current.success(elements);
  }

}
