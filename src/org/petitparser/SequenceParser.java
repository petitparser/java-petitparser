package org.petitparser;

/**
 * A parser that parses a sequence of parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SequenceParser<T> extends AbstractParser<T[]> {

  final Parser<? extends T>[] parsers;

  SequenceParser(Parser<? extends T>... parsers) {
    this.parsers = parsers;
  }

  @Override
  public boolean parse(Context context) {
    int position = context.position;
    Object[] elements = new Object[parsers.length];
    for (int index = 0; index < parsers.length; index++) {
      if (parsers[index].parse(context)) {
        elements[index] = context.result;
      } else {
        context.position = position;
        return false;
      }
    }
    context.result = elements;
    return true;
  }

}
