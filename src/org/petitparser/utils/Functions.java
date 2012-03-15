package org.petitparser.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

/**
 * Constructor and utility methods for functions.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Functions {

  /**
   * Returns a function that returns the first value of a list.
   */
  public static <T> Function<List<T>, T> firstOfList() {
    return nthOfList(0);
  }

  /**
   * Returns a function that returns the last value of a list.
   */
  public static <T> Function<List<T>, T> lastOfList() {
    return nthOfList(-1);
  }

  /**
   * Returns a function that returns the value at the given index. Negative
   * indexes are counted from the end of the list.
   */
  public static <T> Function<List<T>, T> nthOfList(final int index) {
    return new Function<List<T>, T>() {
      @Override
      public T apply(List<T> argument) {
        return argument.get(index < 0 ? argument.size() + index : index);
      }
    };
  }

  /**
   * Returns a function that returns the permutation of a given list. Negative
   * indexes are counted from the end of the list.
   */
  public static <T> Function<List<T>, List<T>> permutationOfList(
      final int... indexes) {
    return new Function<List<T>, List<T>>() {
      @Override
      public List<T> apply(List<T> argument) {
        List<T> result = new ArrayList<T>(indexes.length);
        for (int index : indexes) {
          result.add(argument.get(index < 0 ? argument.size() + index : index));
        }
        return result;
      }
    };
  }
}
