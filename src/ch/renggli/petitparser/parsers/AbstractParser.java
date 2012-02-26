package ch.renggli.petitparser.parsers;

import ch.renggli.petitparser.Parser;

/**
 * An abstract parser that forms the root of all parsers in this package.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class AbstractParser<I, S, O> implements Parser<I, S, O> {

  /**
   * Returns a new parser that is simply wrapped.
   *
   * @category operators
   */
  AbstractParser<I, S, O> wrapped() {
    return new DelegateParser<I, S, O>(this);
  }

  /**
   * Returns a new parser (logical and-predicate) that succeeds whenever the
   * receiver does, but never consumes input.
   *
   * @category operators
   */
  AbstractParser<I, S, O> and() {
    return new AndParser<I, S, O>(this);
  }

  /**
   * Returns a new parser (logical not-predicate) that succeeds whenever the
   * receiver fails, but never consumes input.
   *
   * @category operators
   */
  AbstractParser<I, S, O> not() {
    return new NotParser<I, S, O>(this);
  }

}
