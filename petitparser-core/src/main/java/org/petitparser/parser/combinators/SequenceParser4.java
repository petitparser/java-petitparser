package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.repeating.Tuple4;
import org.petitparser.utils.functions.Function4;

import java.util.Objects;

/**
 * A parser that parses a sequence of 4 parsers and produces a {@link Tuple4}.
 *
 * @param <T1> the result type of the first parser
 * @param <T2> the result type of the second parser
 * @param <T3> the result type of the third parser
 * @param <T4> the result type of the fourth parser
 */
public class SequenceParser4<T1, T2, T3, T4> extends ListParser implements SequentialParser {

  public SequenceParser4(Parser p1, Parser p2, Parser p3, Parser p4) {
    super(p1, p2, p3, p4);
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
    return r4.success(new Tuple4<>(r1.get(), r2.get(), r3.get(), r4.get()));
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
    return parsers[3].fastParseOn(buffer, position);
  }



  @Override
  @SuppressWarnings("unchecked")
  public <S extends Parser> S then(Parser next) {
    return (S) new SequenceParser5<>(parsers[0], parsers[1], parsers[2], parsers[3], next);
  }

  @Override
  public SequenceParser4<T1, T2, T3, T4> copy() {
    return new SequenceParser4<>(parsers[0], parsers[1], parsers[2], parsers[3]);
  }
}
