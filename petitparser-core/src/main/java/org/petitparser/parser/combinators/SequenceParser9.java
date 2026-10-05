package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.utils.tuples.Tuple9;

/**
 * A parser that parses a sequence of 9 parsers and produces a {@link Tuple9}.
 *
 * @param <T1> the result type of the 1st parser
 * @param <T2> the result type of the 2nd parser
 * @param <T3> the result type of the 3rd parser
 * @param <T4> the result type of the 4th parser
 * @param <T5> the result type of the 5th parser
 * @param <T6> the result type of the 6th parser
 * @param <T7> the result type of the 7th parser
 * @param <T8> the result type of the 8th parser
 * @param <T9> the result type of the 9th parser
 */
public class SequenceParser9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends ListParser implements SequentialParser {

  public SequenceParser9(
      Parser p1, Parser p2, Parser p3, Parser p4,
      Parser p5, Parser p6, Parser p7, Parser p8, Parser p9) {
    super(p1, p2, p3, p4, p5, p6, p7, p8, p9);
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
    Result r8 = parsers[7].parseOn(r7);
    if (r8.isFailure()) {
      return r8;
    }
    Result r9 = parsers[8].parseOn(r8);
    if (r9.isFailure()) {
      return r9;
    }
    return r9.success(new Tuple9<>(
        r1.get(), r2.get(), r3.get(), r4.get(),
        r5.get(), r6.get(), r7.get(), r8.get(), r9.get()));
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
    position = parsers[6].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    position = parsers[7].fastParseOn(buffer, position);
    if (position < 0) {
      return position;
    }
    return parsers[8].fastParseOn(buffer, position);
  }




  @Override
  @SuppressWarnings("unchecked")
  public <S extends Parser> S then(Parser next) {
    Parser[] array = new Parser[parsers.length + 1];
    System.arraycopy(parsers, 0, array, 0, parsers.length);
    array[parsers.length] = next;
    return (S) new SequenceParser(array);
  }

  @Override
  public SequenceParser9<T1, T2, T3, T4, T5, T6, T7, T8, T9> copy() {
    return new SequenceParser9<>(
        parsers[0], parsers[1], parsers[2], parsers[3],
        parsers[4], parsers[5], parsers[6], parsers[7], parsers[8]);
  }
}
