package org.petitparser;

import java.util.ArrayList;
import java.util.List;

/**
 * A parser that parses a sequence of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class RepeatingParser<T> extends DelegateParser<T> {

  final int min;
  final int max;

  RepeatingParser(Parser<T> parser) {
    this(parser, 0, Integer.MAX_VALUE);
  }

  RepeatingParser(Parser<T> parser, int min, int max) {
    super(parser);
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean parse(Context context) {
    int position = context.position;
    List<T> elements = new ArrayList<T>();
    while (elements.size() < min) {
      if (super.parse(context)) {
        @SuppressWarnings("unchecked")
        T element = (T) context.result;
        elements.add(element);
      } else {
        context.position = position;
        return false;
      }
    }
    while (elements.size() < max) {
      if (super.parse(context)) {
        @SuppressWarnings("unchecked")
        T element = (T) context.result;
        elements.add(element);
      } else {
        context.result = elements;
        return true;
      }
    }
    context.result = elements;
    return true;
  }

}
