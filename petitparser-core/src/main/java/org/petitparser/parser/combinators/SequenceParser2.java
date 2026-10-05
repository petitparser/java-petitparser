package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.repeating.Tuple2;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * A parser that parses a sequence of 2 parsers and produces a {@link Tuple2}.
 *
 * @param <T1> the result type of the first parser
 * @param <T2> the result type of the second parser
 */
public class SequenceParser2<T1, T2> extends ListParser implements SequentialParser {

  public SequenceParser2(Parser p1, Parser p2) {
    super(p1, p2);
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
    return r2.success(new Tuple2<>(r1.get(), r2.get()));
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    position = parsers[0].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    return parsers[1].fastParseOn(buffer, position);
  }



  @Override
  @SuppressWarnings("unchecked")
  public <S extends Parser> S then(Parser next) {
    return (S) new SequenceParser3<>(parsers[0], parsers[1], next);
  }

  @Override
  public SequenceParser2<T1, T2> copy() {
    return new SequenceParser2<>(parsers[0], parsers[1]);
  }
}
