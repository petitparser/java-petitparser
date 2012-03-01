package org.petitparser.utils;

/**
 * Generic predicate definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 * @param <T> The type of the predicate argument.
 */
public interface Predicate<T> {

  /**
   * Applies the predicate with an argument of type {@code T} and returns a
   * boolean result.
   */
  boolean apply(T argument);

}