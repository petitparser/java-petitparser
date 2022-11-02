package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Failure;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.utils.FailureJoiner;

import java.util.Arrays;

/**
 * A parser that uses the first parser that succeeds.
 */
public class ChoiceParser extends ListParser {

  protected final FailureJoiner failureJoiner;

  public ChoiceParser(Parser... parsers) {
    this(new FailureJoiner.SelectLast(), parsers);
  }

  public ChoiceParser(FailureJoiner failureJoiner, Parser... parsers) {
    super(parsers);
    this.failureJoiner = failureJoiner;
    if (parsers.length == 0) {
      throw new IllegalArgumentException("Choice parser cannot be empty.");
    }
  }

  @Override
  public Result parseOn(Context context) {
    Failure failure = null;
    for (Parser parser : parsers) {
      Result result = parser.parseOn(context);
      if (result.isFailure()) {
        failure = failure == null ? (Failure) result :
            failureJoiner.apply(failure, (Failure) result);
      } else {
        return result;
      }
    }
    return failure;
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    int result = -1;
    for (Parser parser : parsers) {
      result = parser.fastParseOn(buffer, position);
      if (result >= 0) {
        return result;
      }
    }
    return result;
  }

  @Override
  public ChoiceParser or(FailureJoiner failureJoiner, Parser... others) {
    Parser[] array = Arrays.copyOf(parsers, parsers.length + others.length);
    System.arraycopy(others, 0, array, parsers.length, others.length);
    return new ChoiceParser(failureJoiner, array);
  }

  @Override
  public ChoiceParser copy() {
    return new ChoiceParser(failureJoiner, Arrays.copyOf(parsers,
        parsers.length));
  }
}
