package org.petitparser.parser.repeating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * An immutable data structure holding parsed elements and their intervening separators.
 *
 * @param <R> the element type.
 * @param <S> the separator type.
 */
public class SeparatedList<R, S> implements Iterable<Object> {

  private final List<R> elements;
  private final List<S> separators;

  /**
   * Constructs an empty separated list.
   */
  public SeparatedList() {
    this(Collections.emptyList(), Collections.emptyList());
  }

  /**
   * Constructs a separated list with elements and no separators.
   *
   * @param elements the parsed elements.
   * @throws IllegalArgumentException if {@code elements} contains more than one element.
   */
  public SeparatedList(List<R> elements) {
    this(elements, Collections.emptyList());
  }

  /**
   * Constructs an immutable separated list with the given elements and separators.
   *
   * @param elements the parsed elements.
   * @param separators the parsed separators.
   * @throws IllegalArgumentException if the number of separators is inconsistent with the number of elements.
   */
  public SeparatedList(List<R> elements, List<S> separators) {
    this.elements = Collections.unmodifiableList(
        new ArrayList<>(Objects.requireNonNull(elements, "Undefined elements")));
    this.separators = Collections.unmodifiableList(
        new ArrayList<>(Objects.requireNonNull(separators, "Undefined separators")));
    if (Math.max(0, this.elements.size() - 1) != this.separators.size()) {
      throw new IllegalArgumentException(
          "Inconsistent number of elements (" + this.elements.size() +
          ") and separators (" + this.separators.size() + ")");
    }
  }

  /**
   * Returns the unmodifiable list of elements.
   */
  public List<R> getElements() {
    return elements;
  }

  /**
   * Returns the unmodifiable list of separators.
   */
  public List<S> getSeparators() {
    return separators;
  }

  /**
   * Returns an iterable over the elements and interleaved separators in order of appearance.
   */
  public Iterable<Object> getSequential() {
    return this;
  }

  /**
   * Returns an unmodifiable list of the elements and interleaved separators in order of appearance.
   */
  public List<Object> getSequentialList() {
    List<Object> result = new ArrayList<>(elements.size() + separators.size());
    for (int i = 0; i < elements.size(); i++) {
      result.add(elements.get(i));
      if (i < separators.size()) {
        result.add(separators.get(i));
      }
    }
    return Collections.unmodifiableList(result);
  }

  /**
   * Combines the elements by grouping from the left and calling {@code callback} on all consecutive
   * elements with the corresponding separator.
   *
   * @param callback the folding function.
   * @return the folded result.
   * @throws NoSuchElementException if the separated list has no elements.
   */
  public R foldLeft(FoldFunction<R, S> callback) {
    Objects.requireNonNull(callback, "Undefined callback");
    if (elements.isEmpty()) {
      throw new NoSuchElementException("Cannot fold an empty SeparatedList");
    }
    R result = elements.get(0);
    for (int i = 1; i < elements.size(); i++) {
      result = callback.apply(result, separators.get(i - 1), elements.get(i));
    }
    return result;
  }

  /**
   * Combines the elements by grouping from the right and calling {@code callback} on all consecutive
   * elements with the corresponding separator.
   *
   * @param callback the folding function.
   * @return the folded result.
   * @throws NoSuchElementException if the separated list has no elements.
   */
  public R foldRight(FoldFunction<R, S> callback) {
    Objects.requireNonNull(callback, "Undefined callback");
    if (elements.isEmpty()) {
      throw new NoSuchElementException("Cannot fold an empty SeparatedList");
    }
    R result = elements.get(elements.size() - 1);
    for (int i = elements.size() - 2; i >= 0; i--) {
      result = callback.apply(elements.get(i), separators.get(i), result);
    }
    return result;
  }

  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < elements.size() + separators.size();
      }

      @Override
      public Object next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        int i = index++;
        return (i % 2 == 0) ? elements.get(i / 2) : separators.get(i / 2);
      }
    };
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    SeparatedList<?, ?> that = (SeparatedList<?, ?>) other;
    return Objects.equals(elements, that.elements) &&
        Objects.equals(separators, that.separators);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements, separators);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName()).append("(");
    boolean first = true;
    for (Object item : this) {
      if (!first) {
        sb.append(", ");
      }
      sb.append(item);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }
}
