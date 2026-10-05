package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.context.Token;
import org.petitparser.parser.actions.ActionParser;
import org.petitparser.parser.actions.ContinuationParser;
import org.petitparser.parser.actions.FlattenParser;
import org.petitparser.parser.actions.TokenParser;
import org.petitparser.parser.actions.TrimmingParser;
import org.petitparser.parser.actions.WhereParser;
import org.petitparser.parser.combinators.AndParser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.EndOfInputParser;
import org.petitparser.parser.combinators.LabelParser;
import org.petitparser.parser.combinators.NotParser;
import org.petitparser.parser.combinators.OptionalParser;
import org.petitparser.parser.combinators.SequenceParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.combinators.SkipParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.EpsilonParser;
import org.petitparser.parser.primitive.FailureParser;
import org.petitparser.parser.primitive.NewlineParser;
import org.petitparser.parser.primitive.PositionParser;
import org.petitparser.parser.repeating.GreedyRepeatingParser;
import org.petitparser.parser.repeating.LazyRepeatingParser;
import org.petitparser.parser.repeating.PossessiveRepeatingParser;
import org.petitparser.parser.repeating.RepeatingParser;
import org.petitparser.utils.FailureJoiner;
import org.petitparser.utils.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.petitparser.parser.primitive.CharacterParser.any;

/**
 * An abstract parser that forms the root of all parsers in this package.
 */
public abstract class Parser {

  /**
   * Primitive method doing the actual parsing.
   *
   * <p>The method is overridden in concrete subclasses to implement the parser
   * specific logic. The methods takes a parse {@code context} and returns the
   * resulting context, which is either a
   * {@link org.petitparser.context.Success}
   * or {@link org.petitparser.context.Failure} context.
   */
  public abstract Result parseOn(Context context);

  /**
   * Primitive method doing the actual parsing.
   *
   * <p>This method is an optimized version of {@link #parseOn(Context)} that
   * is getting its speed advantage by avoiding any unnecessary memory
   * allocations.
   *
   * <p>The method is overridden in most concrete subclasses to implement the
   * optimized logic. As an input the method takes a {@code buffer} and the
   * current {@code position} in that buffer. It returns a new (positive)
   * position in case of a successful parse, or `-1` in case of a failure.
   *
   * <p>Subclasses don't necessarily have to override this method, since it is
   * emulated using its slower brother.
   */
  public int fastParseOn(String buffer, int position) {
    Result result = parseOn(new Context(buffer, position));
    return result.isSuccess() ? result.getPosition() : -1;
  }

  /**
   * Returns the parse result of the {@code input}.
   */
  public Result parse(String input) {
    return parseOn(new Context(input, 0));
  }

  /**
   * Tests if the {@code input} can be successfully parsed.
   */
  public boolean accept(String input) {
    return fastParseOn(input, 0) >= 0;
  }

  /**
   * Returns a list of all successful overlapping parses of the {@code input}.
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> matches(String input) {
    List<Object> list = new ArrayList<>();
    and().mapWithSideEffects(list::add).seq(any()).or(any()).star()
        .fastParseOn(input, 0);
    return (List<T>) list;
  }

  /**
   * Returns a list of all successful non-overlapping parses of the {@code
   * input}.
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> matchesSkipping(String input) {
    List<Object> list = new ArrayList<>();
    mapWithSideEffects(list::add).or(any()).star().fastParseOn(input, 0);
    return (List<T>) list;
  }

  /**
   * Returns a parser that reports the current input position.
   */
  public static Parser position() {
    return PositionParser.INSTANCE;
  }

  /**
   * Returns a parser that detects newlines platform independently.
   */
  public static Parser newline() {
    return NewlineParser.INSTANCE;
  }

  /**
   * Returns a parser that detects newlines platform independently with a custom failure {@code message}.
   *
   * @param message the failure message.
   */
  public static Parser newline(String message) {
    return new NewlineParser(message);
  }

  /**
   * Returns a parser that consumes nothing and succeeds with {@code null}.
   */
  public static Parser epsilon() {
    return EpsilonParser.INSTANCE;
  }

  /**
   * Returns a parser that consumes nothing and succeeds with {@code value}.
   *
   * @param value the result value.
   */
  public static Parser epsilon(Object value) {
    return new EpsilonParser(value);
  }

  /**
   * Returns a parser that consumes nothing and fails with a default error message.
   */
  public static Parser failure() {
    return new FailureParser();
  }

  /**
   * Returns a parser that consumes nothing and fails with the specified error {@code message}.
   *
   * @param message the failure message.
   */
  public static Parser failure(String message) {
    return FailureParser.withMessage(message);
  }

  /**
   * Returns new parser that accepts the receiver, if possible. The resulting
   * parser returns the result of the receiver, or {@code null} if not
   * applicable.
   */
  public Parser optional() {
    return optional(null);
  }

  /**
   * Returns new parser that accepts the receiver, if possible. The returned
   * value can be provided as {@code otherwise}.
   */
  public Parser optional(Object otherwise) {
    return new OptionalParser(this, otherwise);
  }

  /**
   * Returns a parser that accepts the receiver zero or more times. The
   * resulting parser returns a list of the parse results of the receiver.
   *
   * <p>This is a greedy and blind implementation that tries to consume as much
   * input as possible and that does not consider what comes afterwards.
   */
  public Parser star() {
    return repeat(0, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that parses the receiver zero or more times until it
   * reaches a {@code limit}.
   *
   * <p>This is a greedy non-blind implementation of the {@link Parser#star()}
   * operator. The {@code limit} is not consumed.
   */
  public Parser starGreedy(Parser limit) {
    return repeatGreedy(limit, 0, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that parses the receiver zero or more times until it
   * reaches a {@code limit}.
   *
   * <p>This is a lazy non-blind implementation of the {@link Parser#star()}
   * operator. The {@code limit} is not consumed.
   */
  public Parser starLazy(Parser limit) {
    return repeatLazy(limit, 0, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that accepts the receiver one or more times. The resulting
   * parser returns a list of the parse results of the receiver.
   *
   * <p>This is a greedy and blind implementation that tries to consume as much
   * input as possible and that does not consider what comes afterwards.
   */
  public Parser plus() {
    return repeat(1, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that parses the receiver one or more times until it
   * reaches {@code limit}.
   *
   * <p>This is a greedy non-blind implementation of the {@link Parser#plus()}
   * operator. The {@code limit} is not consumed.
   */
  public Parser plusGreedy(Parser limit) {
    return repeatGreedy(limit, 1, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that parses the receiver one or more times until it
   * reaches a {@code limit}.
   *
   * <p>This is a lazy non-blind implementation of the {@link Parser#plus()}
   * operator. The {@code limit} is not consumed.
   */
  public Parser plusLazy(Parser limit) {
    return repeatLazy(limit, 1, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that accepts the receiver between {@code min} and {@code
   * max} times. The resulting parser returns a list of the parse results of the
   * receiver.
   *
   * <p>This is a greedy and blind implementation that tries to consume as much
   * input as possible and that does not consider what comes afterwards.
   */
  public Parser repeat(int min, int max) {
    return new PossessiveRepeatingParser(this, min, max);
  }

  /**
   * Returns a parser that parses the receiver at least {@code min} and at most
   * {@code max} times until it reaches a {@code limit}.
   *
   * <p>This is a greedy non-blind implementation of the {@link
   * Parser#repeat(int, int)} operator. The {@code limit} is not consumed.
   */
  public Parser repeatGreedy(Parser limit, int min, int max) {
    return new GreedyRepeatingParser(this, limit, min, max);
  }

  /**
   * Returns a parser that parses the receiver at least {@code min} and at most
   * {@code max} times until it reaches a {@code limit}.
   *
   * <p>This is a lazy non-blind implementation of the {@link
   * Parser#repeat(int, int)} operator. The {@code limit} is not consumed.
   */
  public Parser repeatLazy(Parser limit, int min, int max) {
    return new LazyRepeatingParser(this, limit, min, max);
  }

  /**
   * Returns a parser that accepts the receiver exactly {@code count} times. The
   * resulting parser eturns a list of the parse results of the receiver.
   */
  public Parser times(int count) {
    return repeat(count, count);
  }

  /**
   * Returns a parser that accepts the receiver zero or more times and returns a
   * string of the consumed range.
   */
  public Parser starString() {
    return repeatString(0, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that accepts the receiver zero or more times and returns a
   * string of the consumed range. Reports the provided {@code message} in case of an error.
   */
  public Parser starString(String message) {
    return repeatString(0, RepeatingParser.UNBOUNDED, message);
  }

  /**
   * Returns a parser that accepts the receiver one or more times and returns a
   * string of the consumed range.
   */
  public Parser plusString() {
    return repeatString(1, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that accepts the receiver one or more times and returns a
   * string of the consumed range. Reports the provided {@code message} in case of an error.
   */
  public Parser plusString(String message) {
    return repeatString(1, RepeatingParser.UNBOUNDED, message);
  }

  /**
   * Returns a parser that accepts the receiver exactly {@code count} times and
   * returns a string of the consumed range.
   */
  public Parser timesString(int count) {
    return repeatString(count, count);
  }

  /**
   * Returns a parser that accepts the receiver exactly {@code count} times and
   * returns a string of the consumed range. Reports the provided {@code message} in case of an error.
   */
  public Parser timesString(int count, String message) {
    return repeatString(count, count, message);
  }

  /**
   * Returns a parser that accepts the receiver between {@code min} and {@code
   * max} times and returns a string of the consumed range.
   */
  public Parser repeatString(int min, int max) {
    return repeatString(min, max, null);
  }

  /**
   * Returns a parser that accepts the receiver between {@code min} and {@code
   * max} times and returns a string of the consumed range. Reports the provided {@code message} in case of an error.
   */
  public Parser repeatString(int min, int max, String message) {
    return repeat(min, max).flatten(message);
  }

  /**
   * Returns a parser that accepts the receiver followed by {@code others}. The
   * resulting parser returns a list of the parse result of the receiver
   * followed by the parse result of {@code others}.
   *
   * <p>Calling this method on an existing sequence code not nest this sequence
   * into a new one, but instead augments the existing sequence with {@code
   * others}.
   */
  public SequenceParser seq(Parser... others) {
    Parser[] parsers = new Parser[1 + others.length];
    parsers[0] = this;
    System.arraycopy(others, 0, parsers, 1, others.length);
    return new SequenceParser(parsers);
  }

  /**
   * Returns a parser that accepts the receiver or {@code others}. The resulting
   * parser returns the parse result of first succeeding parser (exclusive
   * ordered choice). If all parsers fail, the last parse error is returned.
   */
  public ChoiceParser or(Parser... others) {
    return or(new FailureJoiner.SelectLast(), others);
  }

  /**
   * Returns a parser that accepts the receiver or {@code others}. The resulting
   * parser returns the parse result of first succeeding parser (exclusive
   * ordered choice). If all parers fail, the parse failure is created using
   * the provided {@code failureJoiner}.
   */
  public ChoiceParser or(FailureJoiner failureJoiner, Parser... others) {
    Parser[] parsers = new Parser[1 + others.length];
    parsers[0] = this;
    System.arraycopy(others, 0, parsers, 1, others.length);
    return new ChoiceParser(failureJoiner, parsers);
  }

  /**
   * Returns a parser (logical and-predicate) that succeeds whenever the
   * receiver does, but never consumes input.
   */
  public Parser and() {
    return new AndParser(this);
  }

  /**
   * Returns a parser that is called with its current continuation.
   */
  public Parser callCC(ContinuationParser.ContinuationHandler handler) {
    return new ContinuationParser(this, handler);
  }

  /**
   * Returns a parser (logical not-predicate) that succeeds whenever the
   * receiver fails, but never consumes input.
   */
  public Parser not() {
    return not("unexpected");
  }

  /**
   * Returns a parser (logical not-predicate) that succeeds whenever the
   * receiver fails, but never consumes input.
   */
  public Parser not(String message) {
    return new NotParser(this, message);
  }

  /**
   * Returns a parser that consumes any input token (character), but the
   * receiver.
   */
  public Parser neg() {
    return neg(this + " not expected");
  }

  /**
   * Returns a parser that consumes any input token (character), but the
   * receiver.
   */
  public Parser neg(String message) {
    return not(message).seq(CharacterParser.any()).pick(1);
  }

  /**
   * Returns a parser that discards the result of the receiver, and instead
   * returns a sub-string of the consumed range in the buffer being parsed.
   */
  public Parser flatten() {
    return new FlattenParser(this);
  }

  /**
   * Returns a parser that discards the result of the receiver, and instead
   * returns a sub-string of the consumed range in the buffer being parsed.
   * Reports the provided {@code message} in case of an error.
   */
  public Parser flatten(String message) {
    return new FlattenParser(this, message);
  }

  /**
   * Returns a parser that returns a {@link Token}. The token carries the parsed
   * value of the receiver {@link Token#getValue()}, as well as the consumed
   * input {@link Token#getInput()} from  {@link Token#getStart()} to {@link
   * Token#getStop()} of the input being parsed.
   */
  public Parser token() {
    return new TokenParser(this);
  }

  /**
   * Returns a parser that consumes whitespace before and after the receiver.
   */
  public Parser trim() {
    return trim(CharacterParser.whitespace());
  }

  /**
   * Returns a parser that consumes input on {@code both} sides of the
   * receiver.
   */
  public Parser trim(Parser both) {
    return trim(both, both);
  }

  /**
   * Returns a parser that consumes input {@code before} and {@code after} the
   * receiver.
   */
  public Parser trim(Parser before, Parser after) {
    return new TrimmingParser(this, before, after);
  }

  /**
   * Returns a parser that consumes input {@code before} and {@code after} the
   * receiver, but discards the parse results of {@code before} and {@code after}
   * and only returns the result of the receiver.
   *
   * @param before the parser to consume before the receiver (or {@code null} for epsilon).
   * @param after the parser to consume after the receiver (or {@code null} for epsilon).
   * @return a parser that skips input before and after.
   */
  public Parser skip(Parser before, Parser after) {
    return new SkipParser(this, before, after);
  }

  /**
   * Returns a parser that succeeds only if the receiver consumes the complete
   * input.
   */
  public Parser end() {
    return end("end of input expected");
  }

  /**
   * Returns a parser that succeeds only if the receiver consumes the complete
   * input, otherwise return a failure with the {@code message}.
   */
  public Parser end(String message) {
    return new SequenceParser(this, new EndOfInputParser(message)).pick(0);
  }

  /**
   * Returns a parser that points to the receiver, but can be changed to point
   * to something else at a later point in time.
   */
  public SettableParser settable() {
    return SettableParser.with(this);
  }

  /**
   * Returns a parser that simply defers to its delegate, but that has a {@code label}
   * for debugging purposes.
   *
   * @param label the label of the parser.
   * @return a labeled parser.
   */
  public Parser labeled(String label) {
    return new LabelParser(this, label);
  }

  /**
   * Returns a parser that evaluates a {@code function} as the production action
   * on success of the receiver.
   *
   * @param function production action without side-effects.
   */
  public <A, B> Parser map(Function<A, B> function) {
    return new ActionParser<>(this, function);
  }

  /**
   * Returns a parser that evaluates a {@code function} as the production action
   * on success of the receiver.
   *
   * @param function production action with possible side-effects.
   */
  public <A, B> Parser mapWithSideEffects(Function<A, B> function) {
    return new ActionParser<>(this, function, true);
  }

  /**
   * Returns a parser that evaluates the {@code predicate} on the successful
   * parse result of the receiver.
   *
   * @param predicate the predicate to evaluate.
   * @param <T> the type of the result to filter.
   * @return a filtered parser.
   */
  public <T> Parser where(Predicate<T> predicate) {
    return where(predicate, (String) null);
  }

  /**
   * Returns a parser that evaluates the {@code predicate} on the successful
   * parse result of the receiver.
   *
   * @param predicate the predicate to evaluate.
   * @param message the failure message if the predicate fails.
   * @param <T> the type of the result to filter.
   * @return a filtered parser.
   */
  public <T> Parser where(Predicate<T> predicate, String message) {
    return new WhereParser<>(this, predicate, message);
  }

  /**
   * Returns a parser that evaluates the {@code predicate} on the successful
   * parse result of the receiver.
   *
   * @param predicate the predicate to evaluate.
   * @param failureFactory the failure factory producing a result if the predicate fails.
   * @param <T> the type of the result to filter.
   * @return a filtered parser.
   */
  public <T> Parser where(
      Predicate<T> predicate,
      BiFunction<Context, Result, Result> failureFactory) {
    return new WhereParser<>(this, predicate, failureFactory);
  }

  /**
   * Returns a parser that transform a successful parse result by returning the
   * element at {@code index} of a list. A negative index can be used to access
   * the elements from the back of the list.
   */
  public Parser pick(int index) {
    return map(Functions.nthOfList(index));
  }

  /**
   * Returns a parser that transforms a successful parse result by returning the
   * permuted elements at {@code indexes} of a list. Negative indexes can be
   * used to access the elements from the back of the list.
   */
  public Parser permute(int... indexes) {
    return this.map(Functions.permutationOfList(indexes));
  }

  /**
   * Returns a parser that transforms a successful parse result by returning the
   * given constant {@code value}.
   *
   * @param value the constant value to return.
   * @param <T> the type of the constant value.
   * @return a parser producing the constant value.
   */
  public <T> Parser constant(T value) {
    return map(Functions.constant(value));
  }

  /**
   * Returns a new parser that parses the receiver one or more times, separated
   * by a {@code separator}.
   */
  public Parser separatedBy(Parser separator) {
    return new SequenceParser(this, new SequenceParser(separator, this).star())
        .map((List<List<List<Object>>> input) -> {
          List<Object> result = new ArrayList<>();
          result.add(input.get(0));
          input.get(1).forEach(result::addAll);
          return result;
        });
  }

  /**
   * Returns a new parser that parses the receiver one or more times, separated
   * and possibly ended by a {@code separator}."
   */
  public Parser delimitedBy(Parser separator) {
    return separatedBy(separator).seq(separator.optional())
        .map((List<List<Object>> input) -> {
          List<Object> result = new ArrayList<>(input.get(0));
          if (input.get(1) != null) {
            result.add(input.get(1));
          }
          return result;
        });
  }

  /**
   * Returns a shallow copy of the receiver.
   */
  public abstract Parser copy();

  /**
   * Recursively tests for structural similarity of two parsers.
   *
   * <p>The code can automatically deals with recursive parsers and parsers
   * that refer to other parsers. This code is supposed to be overridden by
   * parsers that add other state.
   */
  public boolean isEqualTo(Parser other) {
    return isEqualTo(other, new HashSet<>());
  }

  /**
   * Recursively tests for structural similarity of two parsers.
   */
  protected boolean isEqualTo(Parser other, Set<Parser> seen) {
    if (this.equals(other) || seen.contains(this)) {
      return true;
    }
    seen.add(this);
    return Objects.equals(getClass(), other.getClass()) &&
        hasEqualProperties(other) && hasEqualChildren(other, seen);
  }

  /**
   * Compares the properties of two parsers.
   *
   * <p>Override this method in all subclasses that add new state.
   */
  protected boolean hasEqualProperties(Parser other) {
    return true;
  }

  /**
   * Compares the children of two parsers.
   *
   * <p>Normally subclasses should not override this method, but instead {@link
   * #getChildren()}.
   */
  protected boolean hasEqualChildren(Parser other, Set<Parser> seen) {
    List<Parser> thisChildren = this.getChildren();
    List<Parser> otherChildren = other.getChildren();
    if (thisChildren.size() != otherChildren.size()) {
      return false;
    }
    for (int i = 0; i < thisChildren.size(); i++) {
      if (!thisChildren.get(i).isEqualTo(otherChildren.get(i), seen)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a list of directly referring parsers.
   */
  public List<Parser> getChildren() {
    return Collections.emptyList();
  }

  /**
   * Replaces the referring parser {@code source} with {@code target}. Does
   * nothing if the parser does not exist.
   */
  public void replace(Parser source, Parser target) {
    // no referring parsers
  }

  /**
   * Returns a human readable string identifying this parser.
   */
  public String toString() {
    return getClass().getSimpleName();
  }
}
