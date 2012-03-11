package org.petitparser.parser;

import java.util.Arrays;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that uses the first parser that succeeds.
 * 
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ChoiceParser<T> extends Parser<T> {

  private final Parser<?>[] parsers;

  private ChoiceParser(Parser<?>... parsers) {
    this.parsers = parsers;
  }

  public ChoiceParser(Parser<?> first, Parser<?> second) {
    this.parsers = new Parser[] { first, second };
  }

  @Override
  public Result<T> parse(Context context) {
    Result<?> result = context.failure("Empty choice");
    for (Parser<?> parser : parsers) {
      result = parser.parse(context);
      if (result.isSuccess()) {
        return result.cast();
      }
    }
    return result.cast();
  }

  @Override
  public <U> Parser<U> or(Parser<? extends U> parser) {
    Parser<?>[] array = Arrays.copyOf(parsers, parsers.length + 1);
    array[parsers.length] = parser;
    return new ChoiceParser<U>(array);
  }

}
