package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A parser that casts a successful {@link List} result to a {@code List<S>}.
 *
 * @param <S> the element type of the resulting list
 */
public class CastListParser<S> extends DelegateParser {

  protected final Class<S> elementClass;

  public CastListParser(Parser delegate) {
    this(delegate, null);
  }

  public CastListParser(Parser delegate, Class<S> elementClass) {
    super(delegate);
    this.elementClass = elementClass;
  }

  public Class<S> getElementClass() {
    return elementClass;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isFailure()) {
      return result;
    }
    List<?> list = result.get();
    if (elementClass != null) {
      List<S> typedList = new ArrayList<>(list.size());
      for (Object element : list) {
        typedList.add(elementClass.cast(element));
      }
      return result.success(typedList);
    }
    return result.success((List<S>) list);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return delegate.fastParseOn(buffer, position);
  }

  @Override
  public CastListParser<S> copy() {
    return new CastListParser<>(delegate, elementClass);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(elementClass, ((CastListParser<?>) other).elementClass);
  }

  @Override
  public String toString() {
    return super.toString() + (elementClass != null ? "[" + elementClass.getSimpleName() + "]" : "");
  }
}
