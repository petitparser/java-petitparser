package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.context.Token;
import org.petitparser.parser.actions.ActionParser;
import org.petitparser.parser.actions.ContinuationParser;
import org.petitparser.parser.actions.FlattenParser;
import org.petitparser.parser.actions.TokenParser;
import org.petitparser.parser.actions.TrimmingParser;
import org.petitparser.parser.characters.CharacterParser;
import org.petitparser.parser.combinators.AndParser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.EndOfInputParser;
import org.petitparser.parser.combinators.NotParser;
import org.petitparser.parser.combinators.OptionalParser;
import org.petitparser.parser.combinators.SequenceParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.repeating.GreedyRepeatingParser;
import org.petitparser.parser.repeating.LazyRepeatingParser;
import org.petitparser.parser.repeating.PossessiveRepeatingParser;
import org.petitparser.parser.repeating.RepeatingParser;
import org.petitparser.utils.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.petitparser.parser.characters.CharacterParser.any;

/**
 * An abstract parser that forms the root of all parsers in this package.
 */
public abstract class Parser {

  /**
   * Primitive method doing the actual parsing.
   */
  public abstract Result parseOn(Context context);

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
    return parse(input).isSuccess();
  }

  /**
   * Returns a list of all successful overlapping parses of the {@code input}.
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> matches(String input) {
    List<Object> list = new ArrayList<>();
    this.and().map(list::add).seq(any()).or(any()).star().parse(input);
    return (List<T>) list;
  }

  /**
   * Returns a list of all successful non-overlapping parses of the {@code input}.
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> matchesSkipping(String input) {
    List<Object> list = new ArrayList<>();
    this.map(list::add).or(any()).star().parse(input);
    return (List<T>) list;
  }

  /**
   * Returns new parser that accepts the receiver, if possible. The resulting parser returns the
   * result of the receiver, or {@code null} if not applicable.
   */
  public Parser optional() {
    return optional(null);
  }

  /**
   * Returns new parser that accepts the receiver, if possible. The returned value can be provided
   * as {@code otherwise}.
   */
  public Parser optional(Object otherwise) {
    return new OptionalParser(this, otherwise);
  }

  /**
   * Returns a parser that accepts the receiver zero or more times. The resulting parser returns a
   * list of the parse results of the receiver.
   * <p>
   * This is a greedy and blind implementation that tries to consume as much input as possible and
   * that does not consider what comes afterwards.
   */
  public Parser star() {
    return repeat(0, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that parses the receiver zero or more times until it reaches a {@code limit}.
   * This is a greedy non-blind implementation of the {@link Parser#star()} operator. The {@code
   * limit} is not consumed.
   */
  public Parser starGreedy(Parser limit) {
    return repeatGreedy(limit, 0, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that parses the receiver zero or more times until it reaches a {@code limit}.
   * This is a lazy non-blind implementation of the {@link Parser#star()} operator. The {@code
   * limit} is not consumed.
   */
  public Parser starLazy(Parser limit) {
    return repeatLazy(limit, 0, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that accepts the receiver one or more times. The resulting parser returns a
   * list of the parse results of the receiver.
   * <p>
   * This is a greedy and blind implementation that tries to consume as much input as possible and
   * that does not consider what comes afterwards.
   */
  public Parser plus() {
    return repeat(1, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that parses the receiver one or more times until it reaches {@code limit}.
   * This is a greedy non-blind implementation of the {@link Parser#plus()} operator. The {@code
   * limit} is not consumed.
   */
  public Parser plusGreedy(Parser limit) {
    return repeatGreedy(limit, 1, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that parses the receiver one or more times until it reaches a {@code limit}.
   * This is a lazy non-blind implementation of the {@link Parser#plus()} operator. The {@code
   * limit} is not consumed.
   */
  public Parser plusLazy(Parser limit) {
    return repeatLazy(limit, 1, RepeatingParser.UNBOUNDED);
  }

  /**
   * Returns a parser that accepts the receiver between {@code min} and {@code max} times. The
   * resulting parser returns a list of the parse results of the receiver.
   * <p>
   * This is a greedy and blind implementation that tries to consume as much input as possible and
   * that does not consider what comes afterwards.
   */
  public Parser repeat(int min, int max) {
    return new PossessiveRepeatingParser(this, min, max);
  }

  /**
   * Returns a parser that parses the receiver at least {@code min} and at most {@code max} times
   * until it reaches a {@code limit}. This is a greedy non-blind implementation of the {@link
   * Parser#repeat(int, int)} operator. The {@code limit} is not consumed.
   */
  public Parser repeatGreedy(Parser limit, int min, int max) {
    return new GreedyRepeatingParser(this, limit, min, max);
  }

  /**
   * Returns a parser that parses the receiver at least {@code min} and at most {@code max} times
   * until it reaches a {@code limit}. This is a lazy non-blind implementation of the {@link
   * Parser#repeat(int, int)} operator. The {@code limit} is not consumed.
   */
  public Parser repeatLazy(Parser limit, int min, int max) {
    return new LazyRepeatingParser(this, limit, min, max);
  }

  /**
   * Returns a parser that accepts the receiver exactly {@code count} times. The resulting parser
   * returns a list of the parse results of the receiver.
   */
  public Parser times(int count) {
    return repeat(count, count);
  }

  /**
   * Returns a parser that accepts the receiver followed by {@code others}. The resulting parser
   * returns a list of the parse result of the receiver followed by the parse result of {@code
   * others}. Calling this method on an existing sequence code not nest this sequence into a new one,
   * but instead augments the existing sequence with {@code others}.
   */
  public Parser seq(Parser... others) {
    Parser[] parsers = new Parser[1 + others.length];
    parsers[0] = this;
    System.arraycopy(others, 0, parsers, 1, others.length);
    return new SequenceParser(parsers);
  }

  /**
   * Returns a parser that accepts the receiver or {@code other}. The resulting parser returns the
   * parse result of the receiver, if the receiver fails it returns the parse result of {@code
   * other} (exclusive ordered choice).
   */
  public Parser or(Parser... others) {
    Parser[] parsers = new Parser[1 + others.length];
    parsers[0] = this;
    System.arraycopy(others, 0, parsers, 1, others.length);
    return new ChoiceParser(parsers);
  }

  /**
   * Returns a parser (logical and-predicate) that succeeds whenever the receiver does, but never
   * consumes input.
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
   * Returns a parser (logical not-predicate) that succeeds whenever the receiver fails, but never
   * consumes input.
   */
  public Parser not() {
    return not("unexpected");
  }

  /**
   * Returns a parser (logical not-predicate) that succeeds whenever the receiver fails, but never
   * consumes input.
   */
  public Parser not(String message) {
    return new NotParser(this, message);
  }

  /**
   * Returns a parser that consumes any input token (character), but the receiver.
   */
  public Parser neg() {
    return neg(this + " not expected");
  }

  /**
   * Returns a parser that consumes any input token (character), but the receiver.
   */
  public Parser neg(String message) {
    return not(message).seq(CharacterParser.any()).pick(1);
  }

  /**
   * Returns a parser that discards the result of the receiver, and returns a sub-string of the
   * consumed range in the string/list being parsed.
   */
  public Parser flatten() {
    return new FlattenParser(this);
  }

  /**
   * Returns a parser that returns a {@link Token}. The token carries the parsed value of the
   * receiver {@link Token#getValue()}, as well as the consumed input {@link Token#getInput()} from
   * {@link Token#getStart()} to {@link Token#getStop()} of the input being parsed.
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
   * Returns a parser that consumes input on {@code both} sides of the receiver.
   */
  public Parser trim(Parser both) {
    return trim(both, both);
  }

  /**
   * Returns a parser that consumes input {@code before} and {@code after} the receiver.
   */
  public Parser trim(Parser before, Parser after) {
    return new TrimmingParser(this, before, after);
  }

  /**
   * Returns a parser that succeeds only if the receiver consumes the complete input.
   */
  public Parser end() {
    return end("end of input expected");
  }

  /**
   * Returns a parser that succeeds only if the receiver consumes the complete input, otherwise
   * return a failure with the {@code message}.
   */
  public Parser end(String message) {
    return new EndOfInputParser(this, message);
  }

  /**
   * Returns a parser that points to the receiver, but can be changed to point to something else at
   * a later point in time.
   */
  public SettableParser settable() {
    return SettableParser.with(this);
  }

  /**
   * Returns a parser that evaluates a {@code function} as the production action on success of the
   * receiver.
   */
  public <A, B> Parser map(Function<A, B> function) {
    return new ActionParser<>(this, function);
  }

  /**
   * Returns a parser that transform a successful parse result by returning the element at {@code
   * index} of a list. A negative index can be used to access the elements from the back of the
   * list.
   */
  public Parser pick(int index) {
    return map(Functions.nthOfList(index));
  }

  /**
   * Returns a parser that transforms a successful parse result by returning the permuted elements
   * at {@code indexes} of a list. Negative indexes can be used to access the elements from the back
   * of the list.
   */
  public Parser permute(int... indexes) {
    return this.map(Functions.permutationOfList(indexes));
  }

  /**
   * Returns a new parser that parses the receiver one or more times, separated
   * by a {@code separator}.
   */
  public Parser separatedBy(Parser separator) {
    return new SequenceParser(this, new SequenceParser(separator, this).star())
        .map(new Function<List<List<List<Object>>>, List<Object>>() {
          @Override
          public List<Object> apply(List<List<List<Object>>> input) {
            List<Object> result = new ArrayList<>();
            result.add(input.get(0));
            input.get(1).forEach(result::addAll);
            return result;
          }
        });
  }

  /**
   * Returns a new parser that parses the receiver one or more times, separated
   * and possibly ended by a {@code separator}."
   */
  public Parser delimitedBy(Parser separator) {
    return separatedBy(separator)
        .seq(separator.optional())
        .map(new Function<List<List<Object>>, List<Object>>() {
          @Override
          public List<Object> apply(List<List<Object>> input) {
            List<Object> result = new ArrayList<>(input.get(0));
            if (input.get(1) != null) {
              result.add(input.get(1));
            }
            return result;
          }
        });
  }

  /**
   * Returns a shallow copy of the receiver.
   */
  public abstract Parser copy();

  /**
   * Recursively tests for structural similarity of two parsers.
   * <p>
   * The code can automatically deals with recursive parsers and parsers that refer to other
   * parsers. This code is supposed to be overridden by parsers that add other state.
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
    return getClass().equals(other.getClass())
        && hasEqualProperties(other)
        && hasEqualChildren(other, seen);
  }

  /**
   * Compares the properties of two parsers.
   * <p>
   * Override this method in all subclasses that add new state.
   */
  protected boolean hasEqualProperties(Parser other) {
    return true;
  }

  /**
   * Compares the children of two parsers.
   * <p>
   * Normally subclasses should not override this method, but instead {@link #getChildren()}.
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
   * Replaces the referring parser {@code source} with {@code target}. Does nothing if the parser
   * does not exist.
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
