package org.petitparser;

import java.util.List;

/**
 * A parser that uses the first parser that succeeds.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ChoiceParser<T> extends ListParser<T> {

  ChoiceParser(List<Parser<T>> parsers) {
    super(parsers);
  }

  @Override
  public boolean parse(Context context) {
    for (Parser<T> parser : parsers) {
      if (parser.parse(context)) {
        return true;
      }
    }
    return false;
  }

}
