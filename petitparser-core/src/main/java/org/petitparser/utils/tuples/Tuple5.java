package org.petitparser.utils.tuples;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable 5-element tuple extending {@link AbstractList}.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @param <T3> the type of the third element
 * @param <T4> the type of the fourth element
 * @param <T5> the type of the fifth element
 */
public class Tuple5<T1, T2, T3, T4, T5> extends AbstractList<Object> implements Tuple {

  protected final T1 first;
  protected final T2 second;
  protected final T3 third;
  protected final T4 fourth;
  protected final T5 fifth;

  public Tuple5(T1 first, T2 second, T3 third, T4 fourth, T5 fifth) {
    this.first = first;
    this.second = second;
    this.third = third;
    this.fourth = fourth;
    this.fifth = fifth;
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

  public T5 fifth() {
    return fifth;
  }

  @Override
  public Object get(int index) {
    return switch (index) {
      case 0 -> first;
      case 1 -> second;
      case 2 -> third;
      case 3 -> fourth;
      case 4 -> fifth;
      default -> throw new IndexOutOfBoundsException("Index: " + index + ", Size: 5");
    };
  }

  @Override
  public int size() {
    return 5;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Tuple5<?, ?, ?, ?, ?> that) {
      return Objects.equals(first, that.first) &&
          Objects.equals(second, that.second) &&
          Objects.equals(third, that.third) &&
          Objects.equals(fourth, that.fourth) &&
          Objects.equals(fifth, that.fifth);
    }
    if (!(other instanceof List<?> that)) {
      return false;
    }
    return that.size() == 5 &&
        Objects.equals(first, that.get(0)) &&
        Objects.equals(second, that.get(1)) &&
        Objects.equals(third, that.get(2)) &&
        Objects.equals(fourth, that.get(3)) &&
        Objects.equals(fifth, that.get(4));
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (first == null ? 0 : first.hashCode());
    result = 31 * result + (second == null ? 0 : second.hashCode());
    result = 31 * result + (third == null ? 0 : third.hashCode());
    result = 31 * result + (fourth == null ? 0 : fourth.hashCode());
    result = 31 * result + (fifth == null ? 0 : fifth.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "[" + first + ", " + second + ", " + third + ", " + fourth + ", " + fifth + "]";
  }
}
