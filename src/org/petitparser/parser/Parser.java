package org.petitparser.parser;

import java.util.List;

import org.petitparser.Parsable;
import org.petitparser.Parsers;
import org.petitparser.utils.Function;
import org.petitparser.utils.Functions;

/**
 * An abstract parser that forms the root of all parsers in this package.
 * 
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class Parser<T> implements Parsable<T> {

  /**
   * Returns a new parser that is simply wrapped.
   */
  public Parser<T> wrapped() {
    return new DelegateParser<T>(this);
  }

  /**
   * Returns a new parser that flattens to a {@link String}.
   */
  public Parser<String> flatten() {
    return new FlattenParser(this);
  }

  /**
   * Returns a new parser (logical and-predicate) that succeeds whenever the
   * receiver does, but never consumes input.
   */
  public Parser<T> and() {
    return new AndParser<T>(this);
  }

  /**
   * Returns a new parser (logical not-predicate) that succeeds whenever the
   * receiver fails, but never consumes input.
   */
  public Parser<T> not(String message) {
    return new NotParser<T>(this, message);
  }

  /**
   * Returns a new parser consumes any input character but the receiver.
   */
  public Parser<Character> negate(String message) {
    Parser<List<Character>> sequence = this.not(message).seq(Parsers.any());
    return sequence.map(Functions.<Character> lastOfList());
  }

  /**
   * Returns a new parser that parses the receiver, if possible.
   */
  public Parser<T> optional() {
    return new OptionalParser<T>(this);
  }

  /**
   * Returns a new parser that parses the receiver zero or more times.
   */
  public Parser<List<T>> star() {
    return repeat(0, Integer.MAX_VALUE);
  }

  /**
   * Returns a new parser that parses the receiver one or more times.
   */
  public Parser<List<T>> plus() {
    return repeat(1, Integer.MAX_VALUE);
  }

  /**
   * Returns a new parser that parses the receiver between {@code min} and
   * {@code max} times.
   */
  public Parser<List<T>> repeat(int min, int max) {
    return new RepeatingParser<T>(this, min, max);
  }

  /**
   * Returns a new parser that parses the receiver, if that fails try with the
   * following parsers.
   */
  public <U> Parser<U> or(Parser<? extends U> parser) {
    return new ChoiceParser<U>(this, parser);
  }

  /**
   * Returns a new parser that first parses the receiver and then the argument.
   */
  public <U> Parser<List<U>> seq(Parser<?> parser) {
    return new SequenceParser<U>(this, parser);
  }

  /**
   * Returns a new parser that performs the given function on success.
   */
  public <U> Parser<U> map(Function<T, U> function) {
    return new ActionParser<T, U>(this, function);
  }

}
