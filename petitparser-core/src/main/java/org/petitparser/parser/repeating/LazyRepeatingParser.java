package org.petitparser.parser.repeating;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A lazy repeating parser, commonly seen in regular expression
 * implementations. It limits its
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
      Result limiter = limit.parseOn(current);
      if (limiter.isSuccess()) {
        return current.success(elements);
      } else {
        if (max != UNBOUNDED && elements.size() >= max) {
          return limiter;
        }
        Result result = delegate.parseOn(current);
        if (result.isFailure()) {
          return limiter;
        }
        elements.add(result.get());
        current = result;
      }
    }
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    int count = 0;
    int current = position;
    while (count < min) {
      int result = delegate.fastParseOn(buffer, current);
      if (result < 0) {
        return -1;
      }
      current = result;
      count++;
    }
    while (true) {
      int limiter = limit.fastParseOn(buffer, current);
      if (limiter >= 0) {
        return current;
      } else {
        if (max != UNBOUNDED && count >= max) {
          return -1;
        }
        int result = delegate.fastParseOn(buffer, current);
        if (result < 0) {
          return -1;
        }
        current = result;
        count++;
      }
    }
  }

  @Override
  public LazyRepeatingParser copy() {
    return new LazyRepeatingParser(delegate, limit, min, max);
  }
}
