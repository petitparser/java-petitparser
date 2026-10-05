package org.petitparser.parser.repeating;

import java.util.List;

/**
 * Common interface and static factory for strongly-typed tuples.
 */
public interface Tuple extends List<Object> {

  /**
   * Creates a 2-tuple.
   */
  static <T1, T2> Tuple2<T1, T2> of(T1 first, T2 second) {
    return new Tuple2<>(first, second);
  }

  /**
   * Creates a 3-tuple.
   */
  static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 first, T2 second, T3 third) {
    return new Tuple3<>(first, second, third);
  }

  /**
   * Creates a 4-tuple.
   */
  static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(
      T1 first, T2 second, T3 third, T4 fourth) {
    return new Tuple4<>(first, second, third, fourth);
  }

  /**
   * Creates a 5-tuple.
   */
  static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(
      T1 first, T2 second, T3 third, T4 fourth, T5 fifth) {
    return new Tuple5<>(first, second, third, fourth, fifth);
  }

  /**
   * Creates a 6-tuple.
   */
  static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(
      T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth) {
    return new Tuple6<>(first, second, third, fourth, fifth, sixth);
  }

  /**
   * Creates a 7-tuple.
   */
  static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(
      T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth, T7 seventh) {
    return new Tuple7<>(first, second, third, fourth, fifth, sixth, seventh);
  }

  /**
   * Creates a 8-tuple.
   */
  static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(
      T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth, T7 seventh, T8 eighth) {
    return new Tuple8<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
  }

  /**
   * Creates a 9-tuple.
   */
  static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> of(
      T1 first, T2 second, T3 third, T4 fourth, T5 fifth, T6 sixth, T7 seventh, T8 eighth, T9 ninth) {
    return new Tuple9<>(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
  }
}
