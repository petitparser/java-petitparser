package org.petitparser.utils;

import java.util.List;

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
    if (index < 0) {
      return new Function<List<T>, T>() {
        @Override
        public T apply(List<T> argument) {
          return argument.get(argument.size() + index);
        }
      };
    } else {
      return new Function<List<T>, T>() {
        @Override
        public T apply(List<T> argument) {
          return argument.get(index);
        }
      };
    }
  }

}
