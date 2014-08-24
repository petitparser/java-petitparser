package org.petitparser.parser;

import java.util.ArrayList;
import java.util.List;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that parses a sequence of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class RepeatingParser extends DelegateParser {

  private final int min, max;

  public RepeatingParser(Parser delegate, int min, int max) {
    super(delegate);
    this.min = min;
    this.max = max;
  }

  @Override
  public Result parse(Context context) {
    Context current = context;
    List<Object> elements = new ArrayList<>();
    while (elements.size() < min) {
      Result result = super.parse(current);
      if (result.isFailure()) {
        return result;
      }
      elements.add(result.get());
      current = result;
    }
    while (elements.size() < max) {
      Result result = super.parse(current);
      if (result.isFailure()) {
        return current.success(elements);
      }
      elements.add(result.get());
      current = result;
    }
    return current.success(elements);
  }

  @Override
  protected boolean matchesProperties(Parser other) {
    RepeatingParser otherRepeatingParser = (RepeatingParser) other;
    return super.matchesProperties(other)
        && min == otherRepeatingParser.min
        && max == otherRepeatingParser.max;
  }

}