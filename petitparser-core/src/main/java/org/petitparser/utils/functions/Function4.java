package org.petitparser.utils.functions;

import java.util.Objects;
import java.util.function.Function;

/**
 * A functional interface that accepts 4 arguments and produces a result.
 *
 * @param <T1> the type of the first argument
 * @param <T2> the type of the second argument
 * @param <T3> the type of the third argument
 * @param <T4> the type of the fourth argument
 * @param <R> the return type
 */
@FunctionalInterface
public interface Function4<T1, T2, T3, T4, R> {

  /**
   * Applies this function to the given arguments.
   */
  R apply(T1 t1, T2 t2, T3 t3, T4 t4);

  /**
   * Returns a composed function that first applies this function, and then applies the {@code after} function.
   */
  default <V> Function4<T1, T2, T3, T4, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after, "Undefined after function");
    return (t1, t2, t3, t4) -> after.apply(apply(t1, t2, t3, t4));
  }
}
