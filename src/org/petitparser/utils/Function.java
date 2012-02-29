package org.petitparser.utils;

/**
 * Generic function definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 * @param <T> The type of the function argument.
 * @param <R> The type of the function result.
 */
public interface Function<T, R> {

  /**
   * Applies the function with an argument of type {@code T} and returns a
   * result of type {@code R}.
   */
  R apply(T argument);

}
