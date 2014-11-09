package org.petitparser.parser.repeating;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A greedy repeating parser, commonly seen in regular expression implementations. It aggressively
 * consumes as much input as possible and then backtracks to meet the 'limit' condition.
 */
public class GreedyRepeatingParser extends LimitedRepeatingParser {

  public GreedyRepeatingParser(Parser delegate, Parser limit, int min, int max) {
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
    List<Context> contexts = new ArrayList<>();
    contexts.add(current);
    while (max == UNBOUNDED || elements.size() < max) {
      Result result = delegate.parseOn(current);
      if (result.isFailure()) {
        break;
      }
      elements.add(result.get());
      contexts.add(current = result);
    }
    while (true) {
      Result stop = limit.parseOn(contexts.get(contexts.size() - 1));
      if (stop.isSuccess()) {
        return contexts.get(contexts.size() - 1).success(elements);
      }
      if (elements.isEmpty()) {
        return stop;
      }
      contexts.remove(contexts.size() - 1);
      elements.remove(elements.size() - 1);
      if (contexts.isEmpty()) {
        return stop;
      }
    }
  }

  @Override
  public Parser copy() {
    return new GreedyRepeatingParser(delegate, limit, min, max);
  }
}

