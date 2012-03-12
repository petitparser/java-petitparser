package org.petitparser.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.petitparser.Combinators;
import org.petitparser.Parsable;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that parses a sequence of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SequenceParser extends ListParser {

  public SequenceParser(Parsable... parsers) {
    super(parsers);
  }

  @Override
  public Result parse(Context context) {
    Context current = context;
    List<Object> elements = new ArrayList<Object>(parsers.length);
    for (Parsable parser : parsers) {
      Result result = parser.parse(current);
      if (result.isFailure()) {
        return result;
      }
      elements.add(result.get());
      current = result;
    }
    return current.success(elements);
  }

  @Override
  public SequenceParser seq(Parsable... more) {
    Parsable[] array = Arrays.copyOf(parsers, parsers.length + more.length);
    System.arraycopy(more, 0, array, parsers.length, more.length);
    return Combinators.seq(array);
  }

}
