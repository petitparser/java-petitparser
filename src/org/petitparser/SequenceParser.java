package org.petitparser;

import java.util.ArrayList;
import java.util.List;

/**
 * A parser that parses a sequence of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SequenceParser<T> extends ListParser<T> {

  SequenceParser(List<Parser<T>> parsers) {
    super(parsers);
  }

  @Override
  public boolean parse(Context context) {
    int position = context.position;
    List<T> elements = new ArrayList<T>(parsers.size());
    for (Parser<T> parser : parsers) {
      if (parser.parse(context)) {
        @SuppressWarnings("unchecked")
        T result = (T) context.result;
        elements.add(result);
      } else {
        context.position = position;
        return false;
      }
    }
    context.result = elements;
    return true;
  }

}
