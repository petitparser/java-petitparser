package org.petitparser.tools;

import com.sun.istack.internal.Nullable;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.SequenceParser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.FailureParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * A builder that allows the simple definition of expression grammars with
 * prefix, postfix, and left- and right-associative infix operators.
 */
public class ExpressionBuilder {

  private final SettableParser loopback = SettableParser.undefined();
  private final List<ExpressionGroup> groups = new ArrayList<>();

  /**
   * Creates a new group of operators that share the same priority.
   */
  public ExpressionGroup group() {
    ExpressionGroup group = new ExpressionGroup();
    groups.add(group);
    return group;
  }

  /**
   * Builds the expression parser.
   */
  public Parser build() {
    Parser parser = FailureParser.withMessage(
        "Highest priority group should define a primitive parser.");
    for (ExpressionGroup group : groups) {
      parser = group.build(parser);
    }
    loopback.set(parser);
    return parser;
  }

  /**
   * Models a group of operators of the same precedence.
   */
  public class ExpressionGroup {

    private final List<Parser> primitives = new ArrayList<>();
    private final List<Parser> wrappers = new ArrayList<>();
    private final List<Parser> prefix = new ArrayList<>();
    private final List<Parser> postfix = new ArrayList<>();
    private final List<Parser> right = new ArrayList<>();
    private final List<Parser> left = new ArrayList<>();

    /**
     * Defines a new primitive or literal {@code parser}.
     */
    public ExpressionGroup primitive(Parser parser) {
      return primitive(parser, null);
    }

    /**
     * Defines a new primitive or literal {@code parser}. Evaluates the optional
     * {@code action} with the parsed {@code value}.
     */
    public <T, R> ExpressionGroup primitive(
        Parser parser, @Nullable Function<T, R> action) {
      primitives.add(action == null ? parser : parser.map(action));
      return this;
    }

    private Parser buildPrimitive(Parser inner) {
      return buildChoice(primitives, inner);
    }

    /**
     * Defines a new wrapper using {@code left} and {@code right} parsers.
     */
    public ExpressionGroup wrapper(Parser left, Parser right) {
      return wrapper(left, right, null);
    }

    /**
     * Defines a new wrapper using {@code left} and {@code right} parsers.
     * Evaluates the optional {@code action} with the parsed {@code left},
     * {@code value} and {@code right}.
     */
    public <T, R> ExpressionGroup wrapper(
        Parser left, Parser right, @Nullable Function<T, R> action) {
      Parser parser = new SequenceParser(left, loopback, right);
      wrappers.add(action == null ? parser : parser.map(action));
      return this;
    }

    private Parser buildWrapper(Parser inner) {
      List<Parser> choices = new ArrayList<>(wrappers);
      choices.add(inner);
      return buildChoice(choices, inner);
    }

    /**
     * Adds a prefix operator {@code parser}.
     */
    public ExpressionGroup prefix(Parser parser) {
      return prefix(parser, null);
    }

    /**
     * Adds a prefix operator {@code parser}. Evaluates the optional {@code
     * action} with the parsed {@code operator} and {@code value}.
     */
    public <T, R> ExpressionGroup prefix(
        Parser parser, @Nullable Function<T, R> action) {
      addTo(prefix, parser, action);
      return this;
    }

    private Parser buildPrefix(Parser inner) {
      if (prefix.isEmpty()) {
        return inner;
      } else {
        Parser sequence = new SequenceParser(buildChoice(prefix).star(), inner);
        return sequence.map((List<List<ExpressionResult>> tuple) -> {
          Object value = tuple.get(1);
          List<ExpressionResult> tuples = tuple.get(0);
          Collections.reverse(tuples);
          for (ExpressionResult result : tuples) {
            value = result.action.apply(Arrays.asList(result.operator, value));
          }
          return value;
        });
      }
    }

    /**
     * Adds a postfix operator {@code parser}.
     */
    public ExpressionGroup postfix(Parser parser) {
      return postfix(parser, null);
    }

    /**
     * Adds a postfix operator {@code parser}. Evaluates the optional {@code
     * action} with the parsed {@code value} and {@code operator}.
     */
    public <T, R> ExpressionGroup postfix(
        Parser parser, @Nullable Function<T, R> action) {
      addTo(postfix, parser, action);
      return this;
    }

    private Parser buildPostfix(Parser inner) {
      if (postfix.isEmpty()) {
        return inner;
      } else {
        Parser sequence =
            new SequenceParser(inner, buildChoice(postfix).star());
        return sequence.map((List<List<ExpressionResult>> tuple) -> {
          Object value = tuple.get(0);
          for (ExpressionResult result : tuple.get(1)) {
            value = result.action.apply(Arrays.asList(value, result.operator));
          }
          return value;
        });
      }
    }

    /**
     * Adds a right-associative operator {@code parser}.
     */
    public ExpressionGroup right(Parser parser) {
      return right(parser, null);
    }

    /**
     * Adds a right-associative operator {@code parser}. Evaluates the optional
     * {@code action} with the parsed {@code left} term, {@code operator}, and
     * {@code right} term.
     */
    public <T, R> ExpressionGroup right(
        Parser parser, @Nullable Function<T, R> action) {
      addTo(right, parser, action);
      return this;
    }

    private Parser buildRight(Parser inner) {
      if (right.isEmpty()) {
        return inner;
      } else {
        Parser sequence = inner.separatedBy(buildChoice(right));
        return sequence.map((List<Object> innerSequence) -> {
          Object result = innerSequence.get(innerSequence.size() - 1);
          for (int i = innerSequence.size() - 2; i > 0; i -= 2) {
            ExpressionResult expressionResult =
                (ExpressionResult) innerSequence.get(i);
            result = expressionResult.action.apply(Arrays
                .asList(innerSequence.get(i - 1), expressionResult.operator,
                    result));
          }
          return result;
        });
      }
    }

    /**
     * Adds a left-associative operator {@code parser}.
     */
    public ExpressionGroup left(Parser parser) {
      return left(parser, null);
    }

    /**
     * Adds a left-associative operator {@code parser}. Evaluates the optional
     * {@code action} with the parsed {@code left} term, {@code operator}, and
     * {@code right} term.
     */
    public <T, R> ExpressionGroup left(
        Parser parser, @Nullable Function<T, R> action) {
      addTo(left, parser, action);
      return this;
    }

    private Parser buildLeft(Parser inner) {
      if (left.isEmpty()) {
        return inner;
      } else {
        Parser sequence = inner.separatedBy(buildChoice(left));
        return sequence.map((List<Object> innerSequence) -> {
          Object result = innerSequence.get(0);
          for (int i = 1; i < innerSequence.size(); i += 2) {
            ExpressionResult expressionResult =
                (ExpressionResult) innerSequence.get(i);
            result = expressionResult.action.apply(Arrays
                .asList(result, expressionResult.operator,
                    innerSequence.get(i + 1)));
          }
          return result;
        });
      }
    }

    // helper to connect operator parser and action, and add to list
    @SuppressWarnings("unchecked")
    private <T, R> void addTo(
        List<Parser> list, Parser parser, @Nullable Function<T, R> action) {
      Function<Object, Object> mapper = action == null ? Function.identity() :
          (Function<Object, Object>) action;
      list.add(parser.map(operator -> new ExpressionResult(operator, mapper)));
    }

    // helper to build an optimal choice parser
    private Parser buildChoice(List<Parser> parsers) {
      return buildChoice(parsers, null);
    }

    private Parser buildChoice(List<Parser> parsers, Parser otherwise) {
      if (parsers.isEmpty()) {
        return otherwise;
      } else if (parsers.size() == 1) {
        return parsers.get(0);
      } else {
        return new ChoiceParser(parsers.toArray(new Parser[parsers.size()]));
      }
    }

    // helper to build the group of parsers
    private Parser build(Parser inner) {
      return buildLeft(buildRight(
          buildPostfix(buildPrefix(buildWrapper(buildPrimitive(inner))))));
    }
  }

  // helper class to associate operators and actions
  private static class ExpressionResult {
    final Object operator;
    final Function<Object, Object> action;

    private ExpressionResult(Object operator, Function<Object, Object> action) {
      this.operator = operator;
      this.action = action;
    }
  }
}
