package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.Objects;

/**
 * A parser that casts the result of its delegate to a given type.
 *
 * @param <R> the input type
 * @param <S> the output type
 */
public class CastParser<R, S> extends DelegateParser {

  protected final Class<S> targetClass;

  public CastParser(Parser delegate) {
    this(delegate, null);
  }

  public CastParser(Parser delegate, Class<S> targetClass) {
    super(delegate);
    this.targetClass = targetClass;
  }

  public Class<S> getTargetClass() {
    return targetClass;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isFailure()) {
      return result;
    }
    Object value = result.get();
    if (targetClass != null) {
      return result.success(targetClass.cast(value));
    }
    return result.success((S) value);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return delegate.fastParseOn(buffer, position);
  }

  @Override
  public CastParser<R, S> copy() {
    return new CastParser<>(delegate, targetClass);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(targetClass, ((CastParser<?, ?>) other).targetClass);
  }

  @Override
  public String toString() {
    return super.toString() + (targetClass != null ? "[" + targetClass.getSimpleName() + "]" : "");
  }
}
