package org.petitparser.tools;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.SequenceParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.FailureParser;
import org.petitparser.parser.repeating.SeparatedList;
import org.petitparser.parser.repeating.SeparatedRepeatingParser;
import org.petitparser.utils.functions.Function3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A builder that allows the simple definition of expression grammars with
 * prefix, postfix, and left- and right-associative infix operators.
 *
 * @param <T> the type of parsed expression values
 */
public class ExpressionBuilder<T> {

  private final SettableParser loopback = SettableParser.undefined();
  private final List<ExpressionGroupBase> groups = new ArrayList<>();
  private final List<Parser> primitives = new ArrayList<>();

  /**
   * Returns the loopback parser for this expression builder.
   * Can be used to loop back to this parser in recursive constructs.
   */
  public SettableParser getLoopback() {
    return loopback;
  }

  /**
   * Returns the loopback parser for this expression builder.
   * Can be used to loop back to this parser in recursive constructs.
   */
  public SettableParser loopback() {
    return loopback;
  }

  /**
   * Defines a new primitive, literal, or value {@code parser}.
   */
  public ExpressionBuilder<T> primitive(Parser parser) {
    return primitive(parser, (Function<Object, T>) null);
  }

  /**
   * Defines a new primitive, literal, or value {@code parser}. Evaluates the optional
   * {@code action} with the parsed {@code value}.
   */
  @SuppressWarnings("unchecked")
  public <R> ExpressionBuilder<T> primitive(
      Parser parser, Function<? super R, ? extends T> action) {
    Objects.requireNonNull(parser, "Undefined parser");
    primitives.add(action == null ? parser : parser.map((Function<Object, Object>) action));
    return this;
  }

  /**
   * Creates a new group of operators that share the same priority.
   */
  public ExpressionGroup<T> group() {
    ExpressionGroup<T> group = new ExpressionGroup<>(loopback);
    groups.add(group);
    return group;
  }

  /**
   * Builds the expression parser.
   */
  public Parser build() {
    Parser parser = primitives.isEmpty()
        ? FailureParser.withMessage("Highest priority group should define a primitive parser.")
        : buildChoice(primitives);
    for (ExpressionGroupBase group : groups) {
      parser = group.build(parser);
    }
    loopback.set(parser);
    return parser;
  }

  static Parser buildChoice(List<Parser> parsers) {
    return buildChoice(parsers, null);
  }

  static Parser buildChoice(List<Parser> parsers, Parser otherwise) {
    if (parsers.isEmpty()) {
      return otherwise;
    } else if (parsers.size() == 1) {
      return parsers.get(0);
    } else {
      return new ChoiceParser(parsers.toArray(new Parser[0]));
    }
  }

  /**
   * Models a group of operators of the same precedence.
   *
   * @param <T> the type of parsed expression values
   */
  public static class ExpressionGroup<T> extends ExpressionGroupBase {

    public ExpressionGroup() {
      super();
    }

    public ExpressionGroup(SettableParser loopback) {
      super(loopback);
    }

    @Override
    public ExpressionGroup<T> primitive(Parser parser) {
      super.primitive(parser);
      return this;
    }

    @Override
    public ExpressionGroup<T> wrapper(Parser left, Parser right) {
      super.wrapper(left, right);
      return this;
    }

    /**
     * Defines a new wrapper using {@code left} and {@code right} parsers.
     * Evaluates the typed {@code action} with the parsed {@code left}, {@code value}, and {@code right}.
     */
    @SuppressWarnings("unchecked")
    public <L, R> ExpressionGroup<T> wrapper(
        Parser left, Parser right, Function3<? super L, ? super T, ? super R, ? extends T> action) {
      Objects.requireNonNull(left, "Undefined left parser");
      Objects.requireNonNull(right, "Undefined right parser");
      Objects.requireNonNull(action, "Undefined action");
      Parser parser = new SequenceParser(left, loopback, right);
      wrappers.add(parser.map((List<Object> tuple) ->
          action.apply((L) tuple.get(0), (T) tuple.get(1), (R) tuple.get(2))));
      return this;
    }

    @Override
    public ExpressionGroup<T> prefix(Parser parser) {
      super.prefix(parser);
      return this;
    }

    /**
     * Adds a prefix operator {@code parser}. Evaluates the typed {@code
     * action} with the parsed {@code operator} and {@code value}.
     */
    @SuppressWarnings("unchecked")
    public <O> ExpressionGroup<T> prefix(
        Parser parser, BiFunction<? super O, ? super T, ? extends T> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      Objects.requireNonNull(action, "Undefined action");
      PrefixAction prefixAction = (op, val) -> action.apply((O) op, (T) val);
      prefix.add(parser.map(operator -> new ExpressionResultPrefix(operator, prefixAction)));
      return this;
    }

    @Override
    public ExpressionGroup<T> postfix(Parser parser) {
      super.postfix(parser);
      return this;
    }

    /**
     * Adds a postfix operator {@code parser}. Evaluates the typed {@code
     * action} with the parsed {@code value} and {@code operator}.
     */
    @SuppressWarnings("unchecked")
    public <O> ExpressionGroup<T> postfix(
        Parser parser, BiFunction<? super T, ? super O, ? extends T> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      Objects.requireNonNull(action, "Undefined action");
      PostfixAction postfixAction = (val, op) -> action.apply((T) val, (O) op);
      postfix.add(parser.map(operator -> new ExpressionResultPostfix(operator, postfixAction)));
      return this;
    }

    @Override
    public ExpressionGroup<T> right(Parser parser) {
      super.right(parser);
      return this;
    }

    /**
     * Adds a right-associative operator {@code parser}. Evaluates the typed
     * {@code action} with the parsed {@code left} term, {@code operator}, and
     * {@code right} term.
     */
    @SuppressWarnings("unchecked")
    public <O> ExpressionGroup<T> right(
        Parser parser, Function3<? super T, ? super O, ? super T, ? extends T> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      Objects.requireNonNull(action, "Undefined action");
      InfixAction infixAction = (l, op, r) -> action.apply((T) l, (O) op, (T) r);
      right.add(parser.map(operator -> new ExpressionResultInfix(operator, infixAction)));
      return this;
    }

    @Override
    public ExpressionGroup<T> left(Parser parser) {
      super.left(parser);
      return this;
    }

    /**
     * Adds a left-associative operator {@code parser}. Evaluates the typed
     * {@code action} with the parsed {@code left} term, {@code operator}, and
     * {@code right} term.
     */
    @SuppressWarnings("unchecked")
    public <O> ExpressionGroup<T> left(
        Parser parser, Function3<? super T, ? super O, ? super T, ? extends T> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      Objects.requireNonNull(action, "Undefined action");
      InfixAction infixAction = (l, op, r) -> action.apply((T) l, (O) op, (T) r);
      left.add(parser.map(operator -> new ExpressionResultInfix(operator, infixAction)));
      return this;
    }

    /**
     * Makes the group optional and instead returns the provided {@code value}.
     */
    @Override
    public ExpressionGroup<T> optional(Object value) {
      super.optional(value);
      return this;
    }
  }

  /**
   * Base class for expression groups providing legacy untyped and method-level generic operator definitions.
   * Being non-generic, it ensures raw-type usage does not undergo method type erasure.
   */
  public static class ExpressionGroupBase {

    final SettableParser loopback;
    final List<Parser> primitives = new ArrayList<>();
    final List<Parser> wrappers = new ArrayList<>();
    final List<Parser> prefix = new ArrayList<>();
    final List<Parser> postfix = new ArrayList<>();
    final List<Parser> right = new ArrayList<>();
    final List<Parser> left = new ArrayList<>();
    private Object optionalValue;
    private boolean hasOptional = false;

    public ExpressionGroupBase() {
      this(SettableParser.undefined());
    }

    public ExpressionGroupBase(SettableParser loopback) {
      this.loopback = Objects.requireNonNull(loopback, "Undefined loopback");
    }

    /**
     * Defines a new primitive or literal {@code parser}.
     */
    public ExpressionGroupBase primitive(Parser parser) {
      return primitive(parser, null);
    }

    /**
     * Defines a new primitive or literal {@code parser}. Evaluates the optional
     * {@code action} with the parsed {@code value}.
     */
    @SuppressWarnings("unchecked")
    public <T, R> ExpressionGroupBase primitive(
        Parser parser, Function<T, R> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      primitives.add(action == null ? parser : parser.map((Function<Object, Object>) action));
      return this;
    }

    private Parser buildPrimitive(Parser inner) {
      if (primitives.isEmpty()) {
        return inner;
      }
      List<Parser> choices = new ArrayList<>(primitives);
      if (inner != null && !(inner instanceof FailureParser)) {
        choices.add(inner);
      }
      return buildChoice(choices, inner);
    }

    /**
     * Defines a new wrapper using {@code left} and {@code right} parsers.
     */
    public ExpressionGroupBase wrapper(Parser left, Parser right) {
      Objects.requireNonNull(left, "Undefined left parser");
      Objects.requireNonNull(right, "Undefined right parser");
      wrappers.add(new SequenceParser(left, loopback, right));
      return this;
    }

    /**
     * Defines a new wrapper using {@code left} and {@code right} parsers.
     * Evaluates the optional {@code action} with the parsed list of {@code [left, value, right]}.
     */
    @SuppressWarnings("unchecked")
    public <T, R> ExpressionGroupBase wrapper(
        Parser left, Parser right, Function<T, R> action) {
      Objects.requireNonNull(left, "Undefined left parser");
      Objects.requireNonNull(right, "Undefined right parser");
      Parser parser = new SequenceParser(left, loopback, right);
      wrappers.add(action == null ? parser : parser.map((Function<Object, Object>) action));
      return this;
    }

    private Parser buildWrapper(Parser inner) {
      if (wrappers.isEmpty()) {
        return inner;
      }
      List<Parser> choices = new ArrayList<>(wrappers);
      choices.add(inner);
      return buildChoice(choices, inner);
    }

    /**
     * Adds a prefix operator {@code parser}.
     */
    public ExpressionGroupBase prefix(Parser parser) {
      return prefix(parser, (Function<List<Object>, Object>) null);
    }

    /**
     * Adds a prefix operator {@code parser}. Evaluates the optional {@code
     * action} with the parsed list of {@code [operator, value]}.
     */
    @SuppressWarnings("unchecked")
    public <T, R> ExpressionGroupBase prefix(
        Parser parser, Function<T, R> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      PrefixAction prefixAction = action == null
          ? (op, val) -> Arrays.asList(op, val)
          : (op, val) -> action.apply((T) Arrays.asList(op, val));
      prefix.add(parser.map(operator -> new ExpressionResultPrefix(operator, prefixAction)));
      return this;
    }

    @SuppressWarnings("unchecked")
    private Parser buildPrefix(Parser inner) {
      if (prefix.isEmpty()) {
        return inner;
      } else {
        Parser sequence = new SequenceParser(buildChoice(prefix).star(), inner);
        return sequence.map((List<Object> tuple) -> {
          Object value = tuple.get(1);
          List<ExpressionResultPrefix> prefixes = (List<ExpressionResultPrefix>) tuple.get(0);
          for (int i = prefixes.size() - 1; i >= 0; i--) {
            value = prefixes.get(i).apply(value);
          }
          return value;
        });
      }
    }

    /**
     * Adds a postfix operator {@code parser}.
     */
    public ExpressionGroupBase postfix(Parser parser) {
      return postfix(parser, (Function<List<Object>, Object>) null);
    }

    /**
     * Adds a postfix operator {@code parser}. Evaluates the optional {@code
     * action} with the parsed list of {@code [value, operator]}.
     */
    @SuppressWarnings("unchecked")
    public <T, R> ExpressionGroupBase postfix(
        Parser parser, Function<T, R> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      PostfixAction postfixAction = action == null
          ? (val, op) -> Arrays.asList(val, op)
          : (val, op) -> action.apply((T) Arrays.asList(val, op));
      postfix.add(parser.map(operator -> new ExpressionResultPostfix(operator, postfixAction)));
      return this;
    }

    @SuppressWarnings("unchecked")
    private Parser buildPostfix(Parser inner) {
      if (postfix.isEmpty()) {
        return inner;
      } else {
        Parser sequence = new SequenceParser(inner, buildChoice(postfix).star());
        return sequence.map((List<Object> tuple) -> {
          Object value = tuple.get(0);
          List<ExpressionResultPostfix> postfixes = (List<ExpressionResultPostfix>) tuple.get(1);
          for (int i = 0; i < postfixes.size(); i++) {
            value = postfixes.get(i).apply(value);
          }
          return value;
        });
      }
    }

    /**
     * Adds a right-associative operator {@code parser}.
     */
    public ExpressionGroupBase right(Parser parser) {
      return right(parser, (Function<List<Object>, Object>) null);
    }

    /**
     * Adds a right-associative operator {@code parser}. Evaluates the optional
     * {@code action} with the parsed list of {@code [left, operator, right]}.
     */
    @SuppressWarnings("unchecked")
    public <T, R> ExpressionGroupBase right(
        Parser parser, Function<T, R> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      InfixAction infixAction = action == null
          ? (l, op, r) -> Arrays.asList(l, op, r)
          : (l, op, r) -> action.apply((T) Arrays.asList(l, op, r));
      right.add(parser.map(operator -> new ExpressionResultInfix(operator, infixAction)));
      return this;
    }

    @SuppressWarnings("unchecked")
    private Parser buildRight(Parser inner) {
      if (right.isEmpty()) {
        return inner;
      } else {
        SeparatedRepeatingParser sequence = inner.plusSeparated(buildChoice(right));
        return sequence.map((SeparatedList<Object, ExpressionResultInfix> sequenceList) ->
            sequenceList.foldRight((left, opResult, right) -> opResult.apply(left, right)));
      }
    }

    /**
     * Adds a left-associative operator {@code parser}.
     */
    public ExpressionGroupBase left(Parser parser) {
      return left(parser, (Function<List<Object>, Object>) null);
    }

    /**
     * Adds a left-associative operator {@code parser}. Evaluates the optional
     * {@code action} with the parsed list of {@code [left, operator, right]}.
     */
    @SuppressWarnings("unchecked")
    public <T, R> ExpressionGroupBase left(
        Parser parser, Function<T, R> action) {
      Objects.requireNonNull(parser, "Undefined parser");
      InfixAction infixAction = action == null
          ? (l, op, r) -> Arrays.asList(l, op, r)
          : (l, op, r) -> action.apply((T) Arrays.asList(l, op, r));
      left.add(parser.map(operator -> new ExpressionResultInfix(operator, infixAction)));
      return this;
    }

    @SuppressWarnings("unchecked")
    private Parser buildLeft(Parser inner) {
      if (left.isEmpty()) {
        return inner;
      } else {
        SeparatedRepeatingParser sequence = inner.plusSeparated(buildChoice(left));
        return sequence.map((SeparatedList<Object, ExpressionResultInfix> sequenceList) ->
            sequenceList.foldLeft((left, opResult, right) -> opResult.apply(left, right)));
      }
    }

    /**
     * Makes the group optional and instead returns the provided {@code value}.
     */
    public ExpressionGroupBase optional(Object value) {
      if (hasOptional) {
        throw new IllegalStateException("At most one optional value expected");
      }
      this.optionalValue = value;
      this.hasOptional = true;
      return this;
    }

    private Parser buildOptional(Parser inner) {
      return hasOptional ? inner.optional(optionalValue) : inner;
    }

    // helper to build the group of parsers
    Parser build(Parser inner) {
      return buildOptional(buildLeft(buildRight(
          buildPostfix(buildPrefix(buildWrapper(buildPrimitive(inner)))))));
    }
  }

  @FunctionalInterface
  interface PrefixAction {
    Object apply(Object operator, Object value);
  }

  static class ExpressionResultPrefix {
    final Object operator;
    final PrefixAction action;

    ExpressionResultPrefix(Object operator, PrefixAction action) {
      this.operator = operator;
      this.action = action;
    }

    Object apply(Object value) {
      return action.apply(operator, value);
    }
  }

  @FunctionalInterface
  interface PostfixAction {
    Object apply(Object value, Object operator);
  }

  static class ExpressionResultPostfix {
    final Object operator;
    final PostfixAction action;

    ExpressionResultPostfix(Object operator, PostfixAction action) {
      this.operator = operator;
      this.action = action;
    }

    Object apply(Object value) {
      return action.apply(value, operator);
    }
  }

  @FunctionalInterface
  interface InfixAction {
    Object apply(Object left, Object operator, Object right);
  }

  static class ExpressionResultInfix {
    final Object operator;
    final InfixAction action;

    ExpressionResultInfix(Object operator, InfixAction action) {
      this.operator = operator;
      this.action = action;
    }

    Object apply(Object left, Object right) {
      return action.apply(left, operator, right);
    }
  }
}
