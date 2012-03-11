package org.petitparser.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that parses a sequence of parsers.
 * 
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SequenceParser<T> extends Parser<List<T>> {

  private final Parser<?>[] parsers;

  private SequenceParser(Parser<?>... parsers) {
    this.parsers = parsers;
  }

  public SequenceParser(Parser<?> first, Parser<?> second) {
    this.parsers = new Parser[] { first, second };
  }

  @Override
  public Result<List<T>> parse(Context context) {
    Context current = context;
    List<T> elements = new ArrayList<T>(parsers.length);
    for (Parser<?> parser : parsers) {
      Result<?> result = parser.parse(current);
      if (result.isFailure()) {
        return result.cast();
      }
      elements.add(result.<T> cast().get());
      current = result;
    }
    return current.success(elements);
  }

  @Override
  public <U> Parser<List<U>> seq(Parser<?> parser) {
    Parser<?>[] array = Arrays.copyOf(parsers, parsers.length + 1);
    array[parsers.length] = parser;
    return new SequenceParser<U>(array);
  }

}
