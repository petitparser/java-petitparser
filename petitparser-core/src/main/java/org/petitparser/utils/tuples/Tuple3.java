package org.petitparser.utils.tuples;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable 3-element tuple extending {@link AbstractList}.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 */
public class Tuple3<T1, T2, T3> extends AbstractList<Object> implements Tuple {

  protected final T1 first;
  protected final T2 second;
  protected final T3 third;

  public Tuple3(T1 first, T2 second, T3 third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public T1 first() {
    return first;
  }

  public T2 second() {
    return second;
  }

  public T3 third() {
    return third;
  }

  @Override
  public Object get(int index) {
    return switch (index) {
      case 0 -> first;
      case 1 -> second;
      case 2 -> third;
      default -> throw new IndexOutOfBoundsException("Index: " + index + ", Size: 3");
    };
  }

  @Override
  public int size() {
    return 3;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Tuple3<?, ?, ?> that) {
      return Objects.equals(first, that.first) &&
          Objects.equals(second, that.second) &&
          Objects.equals(third, that.third);
    }
    if (!(other instanceof List<?> that)) {
      return false;
    }
    return that.size() == 3 &&
        Objects.equals(first, that.get(0)) &&
        Objects.equals(second, that.get(1)) &&
        Objects.equals(third, that.get(2));
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (first == null ? 0 : first.hashCode());
    result = 31 * result + (second == null ? 0 : second.hashCode());
    result = 31 * result + (third == null ? 0 : third.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "[" + first + ", " + second + ", " + third + "]";
  }
}
