package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.Objects;
import java.util.function.Function;

/**
 * A parser that performs a transformation with a given function on the
 * successful parse result of the delegate.
 *
 * @param <T> The type of the function argument.
 * @param <R> The type of the function result.
 */
public class ActionParser<T, R> extends DelegateParser {

  protected final Function<T, R> function;
  protected final boolean hasSideEffects;

  public ActionParser(
      Parser delegate, Function<T, R> function) {
    this(delegate, function, false);
  }

  public ActionParser(
      Parser delegate, Function<T, R> function, boolean hasSideEffects) {
    super(delegate);
    this.function = Objects.requireNonNull(function, "Undefined function");
    this.hasSideEffects = hasSideEffects;
  }

  /**
   * Returns whether this action has side effects.
   */
  public boolean hasSideEffects() {
    return hasSideEffects;
  }

  @Override
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isSuccess()) {
      return result.success(function.apply(result.get()));
    } else {
      return result;
    }
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    // If we know to have side-effects, we have to fall back to the slow mode.
    if (hasSideEffects) {
      Result result = parseOn(new Context(buffer, position));
      return result.isSuccess() ? result.getPosition() : -1;
    }
    return delegate.fastParseOn(buffer, position);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(function, ((ActionParser<T, R>) other).function) &&
        hasSideEffects == ((ActionParser<T, R>) other).hasSideEffects;
  }

  @Override
  public ActionParser<T, R> copy() {
    return new ActionParser<>(delegate, function, hasSideEffects);
  }
}
