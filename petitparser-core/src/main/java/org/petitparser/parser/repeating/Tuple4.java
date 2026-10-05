package org.petitparser.parser.repeating;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable 4-element tuple extending {@link AbstractList}.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 * @param <T4> the type of the fourth element
 */
public class Tuple4<T1, T2, T3, T4> extends AbstractList<Object> implements Tuple {

  protected final T1 first;
  protected final T2 second;
  protected final T3 third;
  protected final T4 fourth;

  public Tuple4(T1 first, T2 second, T3 third, T4 fourth) {
    this.first = first;
    this.second = second;
    this.third = third;
    this.fourth = fourth;
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

  public T4 fourth() {
    return fourth;
  }

  @Override
  public Object get(int index) {
    switch (index) {
      case 0:
        return first;
      case 1:
        return second;
      case 2:
        return third;
      case 3:
        return fourth;
      default:
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: 4");
    }
  }

  @Override
  public int size() {
    return 4;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Tuple4) {
      Tuple4<?, ?, ?, ?> that = (Tuple4<?, ?, ?, ?>) other;
      return Objects.equals(first, that.first) &&
          Objects.equals(second, that.second) &&
          Objects.equals(third, that.third) &&
          Objects.equals(fourth, that.fourth);
    }
    if (!(other instanceof List)) {
      return false;
    }
    List<?> that = (List<?>) other;
    return that.size() == 4 &&
        Objects.equals(first, that.get(0)) &&
        Objects.equals(second, that.get(1)) &&
        Objects.equals(third, that.get(2)) &&
        Objects.equals(fourth, that.get(3));
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (first == null ? 0 : first.hashCode());
    result = 31 * result + (second == null ? 0 : second.hashCode());
    result = 31 * result + (third == null ? 0 : third.hashCode());
    result = 31 * result + (fourth == null ? 0 : fourth.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "[" + first + ", " + second + ", " + third + ", " + fourth + "]";
  }
}
