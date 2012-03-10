package org.petitparser.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.petitparser.Parser;
import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that uses the first parser that succeeds.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ChoiceParser<T> extends AbstractParser<T> {

  private final List<Parser<? extends T>> parsers;

  public ChoiceParser(List<Parser<? extends T>> parsers) {
    this.parsers = parsers;
  }

  @Override
  public Result<T> parse(Context context) {
    Result<? extends T> result = context.failure("Empty choice");
    for (Parser<? extends T> parser : parsers) {
      result = parser.parse(context);
      if (result.isSuccess()) {
        return result.cast();
      }
    }
    return result.cast();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> AbstractParser<U> or(Parser<? extends U> parser, Parser<? extends U>... more) {
    List<Parser<? extends U>> list = new ArrayList<Parser<? extends U>>(parsers.size() + 1 + more.length);
    for (Parser<?> local : parsers) {
      list.add((Parser<? extends U>) local);
    }
    list.add(parser);
    list.addAll(Arrays.asList(more));
    return new ChoiceParser<U>(list);
  }

}
