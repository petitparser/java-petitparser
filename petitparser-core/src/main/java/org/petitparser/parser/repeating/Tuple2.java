package org.petitparser.parser.repeating;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable 2-element tuple extending {@link AbstractList}.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 */
public class Tuple2<T1, T2> extends AbstractList<Object> implements Tuple {

  protected final T1 first;
  protected final T2 second;

  public Tuple2(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }

  public T1 first() {
    return first;
  }

  public T2 second() {
    return second;
  }

  @Override
  public Object get(int index) {
    switch (index) {
      case 0:
        return first;
      case 1:
        return second;
      default:
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 2");
    }
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Tuple2) {
      Tuple2<?, ?> that = (Tuple2<?, ?>) other;
      return Objects.equals(first, that.first) &&
          Objects.equals(second, that.second);
    }
    if (!(other instanceof List)) {
      return false;
    }
    List<?> that = (List<?>) other;
    return that.size() == 2 &&
        Objects.equals(first, that.get(0)) &&
        Objects.equals(second, that.get(1));
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (first == null ? 0 : first.hashCode());
    result = 31 * result + (second == null ? 0 : second.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "[" + first + ", " + second + "]";
  }
}
