package org.petitparser.parser.repeating;

/**
 * A callback for folding elements of a {@link SeparatedList} with their separators.
 *
 * @param <R> the element type.
 * @param <S> the separator type.
 */
@FunctionalInterface
public interface FoldFunction<R, S> {

  /**
   * Combines {@code left}, {@code separator}, and {@code right} into a new value.
   *
   * @param left the left-hand element.
   * @param separator the separator between elements.
   * @param right the right-hand element.
   * @return the combined result.
   */
  R apply(R left, S separator, R right);
}
