package org.petitparser.parser;

import java.util.List;
import java.util.Set;

import org.petitparser.Chars;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.utils.Functions;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * An abstract parser that forms the root of all parsers in this package.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class Parser {

  /**
   * Apply the parser on the given {@code context}.
   */
  public abstract Result parse(Context context);

  /**
   * Returns a new parser that is simply wrapped.
   */
  public Parser wrapped() {
    return new DelegateParser(this);
  }

  /**
   * Returns a new parser that flattens to a {@link String}.
   */
  public Parser flatten() {
    return new FlattenParser(this);
  }

  /**
   * Returns a new parser that consumes whitespace before and after the
   * receiving parser.
   */
  public Parser trim() {
    return trim(Chars.whitespace());
  }

  /**
   * Returns a new parser that consumes and ignores the {@code trimmer}
   * repeatedly before and after the receiving parser.
   */
  public Parser trim(Parser trimmer) {
    return new TrimmingParser(this, trimmer);
  }

  /**
   * Returns a new parser (logical and-predicate) that succeeds whenever the
   * receiver does, but never consumes input.
   */
  public Parser and() {
    return new AndParser(this);
  }

  /**
   * Returns a new parser (logical not-predicate) that succeeds whenever the
   * receiver fails, but never consumes input.
   */
  public Parser not(String message) {
    return new NotParser(this, message);
  }

  /**
   * Returns a new parser that consumes any input character but the receiver.
   */
  public Parser negate() {
    return negate(null);
  }

  /**
   * Returns a new parser that consumes any input character but the receiver.
   */
  public Parser negate(String message) {
    Parser sequence = this.not(message).seq(Chars.any());
    return sequence.map(Functions.lastOfList());
  }

  /**
   * Returns a new parser that parses the receiver, if possible.
   */
  public Parser optional() {
    return new OptionalParser(this);
  }

  /**
   * Returns a new parser that parses the receiver zero or more times.
   */
  public Parser star() {
    return repeat(0, Integer.MAX_VALUE);
  }

  /**
   * Returns a new parser that parses the receiver one or more times.
   */
  public Parser plus() {
    return repeat(1, Integer.MAX_VALUE);
  }

  /**
   * Returns a new parser that parses the receiver exactly {@code count} times.
   */
  public Parser times(int count) {
    return repeat(count, count);
  }

  /**
   * Returns a new parser that parses the receiver between {@code min} and
   * {@code max} times.
   */
  public Parser repeat(int min, int max) {
    return new RepeatingParser(this, min, max);
  }

  /**
   * Returns a new parser that parses the receiver one or more times, separated
   * by a {@code separator}.
   */
  public Parser separatedBy(Parser separator) {
    return new SequenceParser(this, new SequenceParser(separator, this).star()).map(new Function<List<List<Object>>, List<Object>>() {
      @Override
      public List<Object> apply(List<List<Object>> input) {
        List<Object> result = Lists.newArrayList();
        result.add(input.get(0));
        result.addAll(input.get(1));
        return result;
      }
    });
  }

  /**
   * Returns a new parser that parses the receiver, if that fails try with the
   * following parsers.
   */
  public Parser or(Parser... parsers) {
    Parser[] array = new Parser[1 + parsers.length];
    array[0] = this;
    System.arraycopy(parsers, 0, array, 1, parsers.length);
    return new ChoiceParser(array);
  }

  /**
   * Returns a new parser that first parses the receiver and then the argument.
   */
  public Parser seq(Parser... parsers) {
    Parser[] array = new Parser[1 + parsers.length];
    array[0] = this;
    System.arraycopy(parsers, 0, array, 1, parsers.length);
    return new SequenceParser(array);
  }

  /**
   * Returns a new parser that performs the given function on success.
   */
  public <T, R> Parser map(Function<T, R> function) {
    return new ActionParser<T, R>(this, function);
  }

  /**
   * Returns a new parser that succeeds at the end of the input and return the
   * result of the receiver.
   */
  public Parser end() {
    return end("end of input expected");
  }

  /**
   * Returns a new parser that succeeds at the end of the input and return the
   * result of the receiver.
   */
  public Parser end(String message) {
    return new EndOfInputParser(this, message);
  }

  /**
   * Replaces the referring parser {@code source} with {@code target}. Does
   * nothing if the parser does not exist.
   */
  public void replace(Parser source, Parser target) {
    // no referring parsers
  }

  /**
   * Returns a list of directly referring parsers.
   */
  public Set<Parser> children() {
    return Sets.newHashSet();
  }

}
