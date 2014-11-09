package org.petitparser.parser.repeating;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A lazy repeating parser, commonly seen in regular expression implementations. It limits its
 * consumption to meet the 'limit' condition as early as possible.
 */
public class LazyRepeatingParser extends LimitedRepeatingParser {

  public LazyRepeatingParser(Parser delegate, Parser limit, int min, int max) {
    super(delegate, limit, min, max);
  }

  @Override
  public Result parseOn(Context context) {
    Context current = context;
    List<Object> elements = new ArrayList<>();
    while (elements.size() < min) {
      Result result = delegate.parseOn(current);
      if (result.isFailure()) {
        return result;
      }
      elements.add(result.get());
      current = result;
    }
    while (true) {
      Result stop = limit.parseOn(current);
      if (stop.isSuccess()) {
        return current.success(elements);
      } else {
        if (max != UNBOUNDED && elements.size() >= max) {
          return stop;
        }
        Result result = delegate.parseOn(current);
        if (result.isFailure()) {
          return stop;
        }
        elements.add(result.get());
        current = result;
      }
    }
  }

  @Override
  public Parser copy() {
    return new LazyRepeatingParser(delegate, limit, min, max);
  }
}
