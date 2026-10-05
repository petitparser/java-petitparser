package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.utils.tuples.Tuple6;

/**
 * A parser that parses a sequence of 6 parsers and produces a {@link Tuple6}.
 *
 * @param <T1> the result type of the first parser
 * @param <T2> the result type of the second parser
 * @param <T3> the result type of the third parser
 * @param <T4> the result type of the fourth parser
 * @param <T5> the result type of the fifth parser
 * @param <T6> the result type of the sixth parser
 */
public class SequenceParser6<T1, T2, T3, T4, T5, T6> extends ListParser implements SequentialParser {

  public SequenceParser6(
      Parser p1, Parser p2, Parser p3, Parser p4, Parser p5, Parser p6) {
    super(p1, p2, p3, p4, p5, p6);
  }

  @Override
  public Result parseOn(Context context) {
    Result r1 = parsers[0].parseOn(context);
    if (r1.isFailure()) {
      return r1;
    }
    Result r2 = parsers[1].parseOn(r1);
    if (r2.isFailure()) {
      return r2;
    }
    Result r3 = parsers[2].parseOn(r2);
    if (r3.isFailure()) {
      return r3;
    }
    Result r4 = parsers[3].parseOn(r3);
    if (r4.isFailure()) {
      return r4;
    }
    Result r5 = parsers[4].parseOn(r4);
    if (r5.isFailure()) {
      return r5;
    }
    Result r6 = parsers[5].parseOn(r5);
    if (r6.isFailure()) {
      return r6;
    }
    return r6.success(new Tuple6<>(
        r1.get(), r2.get(), r3.get(), r4.get(), r5.get(), r6.get()));
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    position = parsers[0].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    position = parsers[1].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    position = parsers[2].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    position = parsers[3].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    position = parsers[4].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    return parsers[5].fastParseOn(buffer, position);
  }



  @Override
  @SuppressWarnings("unchecked")
  public <S extends Parser> S then(Parser next) {
    return (S) new SequenceParser7<>(
        parsers[0], parsers[1], parsers[2], parsers[3], parsers[4], parsers[5], next);
  }

  @Override
  public SequenceParser6<T1, T2, T3, T4, T5, T6> copy() {
    return new SequenceParser6<>(
        parsers[0], parsers[1], parsers[2], parsers[3], parsers[4], parsers[5]);
  }
}
