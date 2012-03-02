package org.petitparser;

import java.util.List;

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
  public AbstractParser<T> wrapped() {
    return new DelegateParser<T>(this);
  }

  /**
   * Returns a new parser (logical and-predicate) that succeeds whenever the
   * receiver does, but never consumes input.
   *
   * @category operators
   */
  public AbstractParser<T> and() {
    return new AndParser<T>(this);
  }

  /**
   * Returns a new parser (logical not-predicate) that succeeds whenever the
   * receiver fails, but never consumes input.
   *
   * @category operators
   */
  public AbstractParser<T> not(String message) {
    return new NotParser<T>(this, message);
  }

  /**
   * Returns a new parser that parses the receiver, if possible.
   *
   * @category operators
   */
  public AbstractParser<T> optional() {
    return new OptionalParser<T>(this);
  }

  /**
   * Returns a new parser that parses the receiver zero or more times. This is a
   * greedy and blind implementation that tries to consume as much input as
   * possible and it does not consider what comes afterwards.
   *
   * @category repeating operators
   */
  public AbstractParser<List<T>> star() {
    return new RepeatingParser<T>(this, 0, Integer.MAX_VALUE);
  }

  /**
   * Returns a new parser that parses the receiver one or more times.
   *
   * @category repeating operators
   */
  public AbstractParser<List<T>> plus() {
    return new RepeatingParser<T>(this, 1, Integer.MAX_VALUE);
  }

}
