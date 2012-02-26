package org.petitparser.parser;

import org.petitparser.Parser;

/**
 * An abstract parser that forms the root of all parsers in this package.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class AbstractParser<I, O> implements Parser<I, O> {

  /**
   * Returns a new parser that is simply wrapped.
   *
   * @category operators
   */
  AbstractParser<I, O> wrapped() {
    return new DelegateParser<I, O>(this);
  }

  /**
   * Returns a new parser (logical and-predicate) that succeeds whenever the
   * receiver does, but never consumes input.
   *
   * @category operators
   */
  AbstractParser<I, O> and() {
    return new AndParser<I, O>(this);
  }

  /**
   * Returns a new parser (logical not-predicate) that succeeds whenever the
   * receiver fails, but never consumes input.
   *
   * @category operators
   */
  AbstractParser<I, O> not() {
    return new NotParser<I, O>(this);
  }

}
