package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.repeating.Tuple;

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

  public int[] getIndexes() {
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
    switch (indexes.length) {
      case 0:
        return Collections.emptyList();
      case 1: {
        int idx = indexes[0];
        return Collections.singletonList(list.get(idx < 0 ? size + idx : idx));
      }
      case 2:
        return Tuple.of(
            list.get(idx(0, size)),
            list.get(idx(1, size)));
      case 3:
        return Tuple.of(
            list.get(idx(0, size)),
            list.get(idx(1, size)),
            list.get(idx(2, size)));
      case 4:
        return Tuple.of(
            list.get(idx(0, size)),
            list.get(idx(1, size)),
            list.get(idx(2, size)),
            list.get(idx(3, size)));
      case 5:
        return Tuple.of(
            list.get(idx(0, size)),
            list.get(idx(1, size)),
            list.get(idx(2, size)),
            list.get(idx(3, size)),
            list.get(idx(4, size)));
      case 6:
        return Tuple.of(
            list.get(idx(0, size)),
            list.get(idx(1, size)),
            list.get(idx(2, size)),
            list.get(idx(3, size)),
            list.get(idx(4, size)),
            list.get(idx(5, size)));
      case 7:
        return Tuple.of(
            list.get(idx(0, size)),
            list.get(idx(1, size)),
            list.get(idx(2, size)),
            list.get(idx(3, size)),
            list.get(idx(4, size)),
            list.get(idx(5, size)),
            list.get(idx(6, size)));
      case 8:
        return Tuple.of(
            list.get(idx(0, size)),
            list.get(idx(1, size)),
            list.get(idx(2, size)),
            list.get(idx(3, size)),
            list.get(idx(4, size)),
            list.get(idx(5, size)),
            list.get(idx(6, size)),
            list.get(idx(7, size)));
      case 9:
        return Tuple.of(
            list.get(idx(0, size)),
            list.get(idx(1, size)),
            list.get(idx(2, size)),
            list.get(idx(3, size)),
            list.get(idx(4, size)),
            list.get(idx(5, size)),
            list.get(idx(6, size)),
            list.get(idx(7, size)),
            list.get(idx(8, size)));
      default: {
        List<Object> result = new ArrayList<>(indexes.length);
        for (int index : indexes) {
          result.add(list.get(index < 0 ? size + index : index));
        }
        return result;
      }
    }
  }

  private int idx(int i, int size) {
    int index = indexes[i];
    return index < 0 ? size + index : index;
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
