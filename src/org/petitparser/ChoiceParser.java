package org.petitparser;

/**
 * A parser that uses the first parser that succeeds.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ChoiceParser<T> extends AbstractParser<T> {

  final Parser<? extends T>[] parsers;

  ChoiceParser(Parser<? extends T>... parsers) {
    this.parsers = parsers;
  }

  @Override
  public boolean parse(Context context) {
    for (Parser<?> parser : parsers) {
      if (parser.parse(context)) {
        return true;
      }
    }
    return false;
  }

}
