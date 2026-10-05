package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A parser that transforms a successful parse result by returning the
 * permuted elements at {@code indexes} of a list. Negative indexes can be
 * used to access the elements from the back of the list.
 */
public class PermuteParser extends DelegateParser {

  protected final int[] indexes;

  public PermuteParser(Parser delegate, int... indexes) {
    super(delegate);
    this.indexes = Objects.requireNonNull(indexes, "Undefined indexes").clone();
  }

  /**
   * Returns a copy of the permutation indices.
   */
  public int[] getIndices() {
    return indexes.clone();
  }


  @Override
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isFailure()) {
      return result;
    }
    List<?> list = result.get();
    int size = list.size();
    Object permuted = permuteList(list, size);
    return result.success(permuted);
  }

  private Object permuteList(List<?> list, int size) {
    if (indexes.length == 0) {
      return Collections.emptyList();
    } else {
      List<Object> result = new ArrayList<>(indexes.length);
      for (int index : indexes) {
        result.add(list.get(index < 0 ? size + index : index));
      }
      return result;
    }
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return delegate.fastParseOn(buffer, position);
  }

  @Override
  public PermuteParser copy() {
    return new PermuteParser(delegate, indexes);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Arrays.equals(indexes, ((PermuteParser) other).indexes);
  }

  @Override
  public String toString() {
    return super.toString() + Arrays.toString(indexes);
  }
}
