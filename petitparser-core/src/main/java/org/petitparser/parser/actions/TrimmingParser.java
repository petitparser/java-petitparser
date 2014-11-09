package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A parser that silently consumes a before and after the delegate parser.
 */
public class TrimmingParser extends DelegateParser {

  private Parser left;
  private Parser right;

  public TrimmingParser(Parser delegate, Parser trimmer) {
    this(delegate, trimmer, trimmer);
  }

  public TrimmingParser(Parser delegate, Parser left, Parser right) {
    super(delegate);
    this.left = Objects.requireNonNull(left);
    this.right = Objects.requireNonNull(right);
  }

  @Override
  public Result parseOn(Context context) {
    Result leftResult = consume(left, context);
    Result delegateResult = delegate.parseOn(leftResult);
    if (delegateResult.isFailure()) {
      return delegateResult;
    }
    Result rightResult = consume(right, delegateResult);
    return rightResult.success(delegateResult.get());
  }

  private Result consume(Parser parser, Context context) {
    Result result = parser.parseOn(context);
    while (result.isSuccess()) {
      result = parser.parseOn(result);
    }
    return result;
  }

  @Override
  public void replace(Parser source, Parser target) {
    super.replace(source, target);
    if (left == source) {
      left = target;
    }
    if (right == source) {
      left = target;
    }
  }

  @Override
  public List<Parser> getChildren() {
    return Arrays.asList(delegate, left, right);
  }

  @Override
  public Parser copy() {
    return new TrimmingParser(delegate, left, right);
  }
}
