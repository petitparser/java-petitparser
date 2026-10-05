package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.List;

/**
 * A parser that transforms a successful parse result by returning the element at
 * {@code index} of a list. A negative index can be used to access the elements
 * from the back of the list.
 *
 * @param <T> the picked element type
 */
public class PickParser<T> extends DelegateParser {

  protected final int index;

  public PickParser(Parser delegate, int index) {
    super(delegate);
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isFailure()) {
      return result;
    }
    List<?> list = result.get();
    int size = list.size();
    int actualIndex = index < 0 ? size + index : index;
    return result.success((T) list.get(actualIndex));
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return delegate.fastParseOn(buffer, position);
  }

  @Override
  public PickParser<T> copy() {
    return new PickParser<>(delegate, index);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) && index == ((PickParser<?>) other).index;
  }

  @Override
  public String toString() {
    return super.toString() + "[" + index + "]";
  }
}
