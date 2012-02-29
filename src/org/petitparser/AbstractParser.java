package org.petitparser;

/**
 * An abstract parser that forms the root of all parsers in this package.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class AbstractParser<T> implements Parser<T> {

  /**
   * Returns a new parser that is simply wrapped.
   *
   * @category operators
   */
  AbstractParser<T> wrapped() {
    return new DelegateParser<T>(this);
  }

  /**
   * Returns a new parser (logical and-predicate) that succeeds whenever the
   * receiver does, but never consumes input.
   *
   * @category operators
   */
  AbstractParser<T> and() {
    return new AndParser<T>(this);
  }

  /**
   * Returns a new parser (logical not-predicate) that succeeds whenever the
   * receiver fails, but never consumes input.
   *
   * @category operators
   */
  AbstractParser<T> not() {
    return new NotParser<T>(this);
  }

}
