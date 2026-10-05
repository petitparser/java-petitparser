package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.EpsilonParser;

import java.util.Arrays;
import java.util.List;

/**
 * A parser that silently consumes input of another parser before and after its delegate.
 */
public class SkipParser extends DelegateParser implements SequentialParser {

  protected Parser before;
  protected Parser after;

  /**
   * Constructs a skip parser that delegates to {@code delegate} with empty before and after parsers.
   *
   * @param delegate the delegate parser.
   */
  public SkipParser(Parser delegate) {
    this(delegate, null, null);
  }

  /**
   * Constructs a skip parser with the specified before and after parsers.
   *
   * @param delegate the delegate parser.
   * @param before the parser to consume before the delegate (or {@code null} for epsilon).
   * @param after the parser to consume after the delegate (or {@code null} for epsilon).
   */
  public SkipParser(Parser delegate, Parser before, Parser after) {
    super(delegate);
    this.before = before != null ? before : EpsilonParser.INSTANCE;
    this.after = after != null ? after : EpsilonParser.INSTANCE;
  }

  /**
   * Returns the parser executed before the delegate.
   */
  public Parser getBefore() {
    return before;
  }

  /**
   * Returns the parser executed after the delegate.
   */
  public Parser getAfter() {
    return after;
  }

  @Override
  public Result parseOn(Context context) {
    Result beforeResult = before.parseOn(context);
    if (beforeResult.isFailure()) {
      return beforeResult;
    }
    Result result = delegate.parseOn(beforeResult);
    if (result.isFailure()) {
      return result;
    }
    Result afterResult = after.parseOn(result);
    if (afterResult.isFailure()) {
      return afterResult;
    }
    return afterResult.success(result.get());
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    position = before.fastParseOn(buffer, position);
    if (position < 0) {
      return -1;
    }
    position = delegate.fastParseOn(buffer, position);
    if (position < 0) {
      return -1;
    }
    return after.fastParseOn(buffer, position);
  }

  @Override
  public List<Parser> getChildren() {
    return Arrays.asList(before, delegate, after);
  }

  @Override
  public void replace(Parser source, Parser target) {
    super.replace(source, target);
    if (before == source) {
      before = target;
    }
    if (after == source) {
      after = target;
    }
  }

  @Override
  public SkipParser copy() {
    return new SkipParser(delegate, before, after);
  }
}
