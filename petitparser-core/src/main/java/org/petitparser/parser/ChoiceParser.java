package org.petitparser.parser;

import java.util.Arrays;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that uses the first parser that succeeds.
 */
public class ChoiceParser extends ListParser {

  public ChoiceParser(Parser... parsers) {
    super(parsers);
  }

  @Override
  public Result parse(Context context) {
    Result result = context.failure("Empty choice");
    for (Parser parser : parsers) {
      result = parser.parse(context);
      if (result.isSuccess()) {
        return result;
      }
    }
    return result;
  }

  @Override
  public Parser or(Parser... more) {
    Parser[] array = Arrays.copyOf(parsers, parsers.length + more.length);
    System.arraycopy(more, 0, array, parsers.length, more.length);
    return new ChoiceParser(array);
  }

}
