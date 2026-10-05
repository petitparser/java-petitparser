package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.repeating.Tuple7;
import org.petitparser.utils.functions.Function7;

import java.util.Objects;

/**
 * A parser that parses a sequence of 7 parsers and produces a {@link Tuple7}.
 *
 * @param <T1> the result type of the first parser
 * @param <T2> the result type of the second parser
 * @param <T3> the result type of the third parser
 * @param <T4> the result type of the fourth parser
 * @param <T5> the result type of the fifth parser
 * @param <T6> the result type of the sixth parser
 * @param <T7> the result type of the seventh parser
 */
public class SequenceParser7<T1, T2, T3, T4, T5, T6, T7> extends ListParser implements SequentialParser {

  public SequenceParser7(
      Parser p1, Parser p2, Parser p3, Parser p4, Parser p5, Parser p6, Parser p7) {
    super(p1, p2, p3, p4, p5, p6, p7);
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
    Result r7 = parsers[6].parseOn(r6);
    if (r7.isFailure()) {
      return r7;
    }
    return r7.success(new Tuple7<>(
        r1.get(), r2.get(), r3.get(), r4.get(), r5.get(), r6.get(), r7.get()));
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
    position = parsers[5].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    return parsers[6].fastParseOn(buffer, position);
  }


  /**
   * Maps the results of this sequence using a 7-argument function.
   */
  @SuppressWarnings("unchecked")
  public <R> Parser map(Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> function) {
    Objects.requireNonNull(function, "Undefined map function");
    return super.map(tuple -> {
      Tuple7<T1, T2, T3, T4, T5, T6, T7> t = (Tuple7<T1, T2, T3, T4, T5, T6, T7>) tuple;
      return function.apply(t.first(), t.second(), t.third(), t.fourth(), t.fifth(), t.sixth(), t.seventh());
    });
  }

  @Override
  public SequenceParser7<T1, T2, T3, T4, T5, T6, T7> copy() {
    return new SequenceParser7<>(
        parsers[0], parsers[1], parsers[2], parsers[3], parsers[4], parsers[5], parsers[6]);
  }
}
