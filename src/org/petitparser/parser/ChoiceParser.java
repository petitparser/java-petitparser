package org.petitparser.parser;

import java.util.Arrays;

import org.petitparser.Combinators;
import org.petitparser.Parsable;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that uses the first parser that succeeds.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ChoiceParser extends ListParser {

  public ChoiceParser(Parsable... parsers) {
    super(parsers);
  }

  @Override
  public Result parse(Context context) {
    Result result = context.failure("Empty choice");
    for (Parsable parser : parsers) {
      result = parser.parse(context);
      if (result.isSuccess()) {
        return result;
      }
    }
    return result;
  }

  @Override
  public ChoiceParser or(Parsable... more) {
    Parsable[] array = Arrays.copyOf(parsers, parsers.length + more.length);
    System.arraycopy(more, 0, array, parsers.length, more.length);
    return Combinators.or(array);
  }

}
