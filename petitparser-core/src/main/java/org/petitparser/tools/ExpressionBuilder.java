package org.petitparser.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.petitparser.Parsers;
import org.petitparser.parser.ChoiceParser;
import org.petitparser.parser.Parser;
import org.petitparser.parser.SequenceParser;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

/**
 * A builder that allows the simple definition of expression grammars with
 * prefix, postfix, and left- and right-associative infix operators.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ExpressionBuilder {

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
    Parser parser = Parsers.failure("Highest priority group should define a primitive parser.");
    for (ExpressionGroup group : groups) {
      parser = group.build(parser);
    }
    return parser;
  }

  /**
   * Models a group of operators of the same precedence.
   */
  public static class ExpressionGroup {

    /**
     * Defines a new primitive or literal {@code parser}.
     */
    public ExpressionGroup primitive(Parser parser) {
      primitives.add(parser);
      return this;
    }

    private Parser buildPrimitive(Parser inner) {
      return buildChoice(primitives, inner);
    }

    private final List<Parser> primitives = new ArrayList<>();

    /**
     * Adds a prefix operator {@code parser}.
     */
    public ExpressionGroup prefix(Parser parser) {
      addTo(prefix, parser, Functions.identity());
      return this;
    }

    /**
     * Adds a prefix operator {@code parser}. Evaluates the optional
     * {@code action} with the parsed {@code operator} and {@code value}.
     */
    public <T, R> ExpressionGroup prefix(Parser parser, Function<T, R> action) {
      addTo(prefix, parser, action);
      return this;
    }

    private Parser buildPrefix(Parser inner) {
      if (prefix.isEmpty()) {
        return inner;
      } else {
        Parser sequence = new SequenceParser(buildChoice(prefix).star(), inner);
        return sequence.map(new Function<List<Object>, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(List<Object> tuple) {
            Object value = tuple.get(1);
            for (ExpressionResult result : Lists.reverse((List<ExpressionResult>) tuple.get(0))) {
              value = result.action.apply(Arrays.asList(result.operator, value));
            }
            return value;
          }
        });
      }
    }

    private final List<Parser> prefix = new ArrayList<>();

    /**
     * Adds a postfix operator {@code parser}.
     */
    public ExpressionGroup postfix(Parser parser) {
      addTo(postfix, parser, Functions.identity());
      return this;
    }

    /**
     * Adds a postfix operator {@code parser}. Evaluates the optional
     * {@code action} with the parsed {@code value} and {@code operator}.
     */
    public <T, R> ExpressionGroup postfix(Parser parser, Function<T, R> action) {
      addTo(postfix, parser, action);
      return this;
    }

    private Parser buildPostfix(Parser inner) {
      if (postfix.isEmpty()) {
        return inner;
      } else {
        Parser sequence = new SequenceParser(inner, buildChoice(postfix).star());
        return sequence.map(new Function<List<Object>, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(List<Object> tuple) {
            Object value = tuple.get(0);
            for (ExpressionResult result : (List<ExpressionResult>) tuple.get(1)) {
              value = result.action.apply(Arrays.asList(value, result.operator));
            }
            return value;
          }
        });
      }
    }

    private final List<Parser> postfix = new ArrayList<>();

    /**
     * Adds a right-associative operator {@code parser}.
     */
    public ExpressionGroup right(Parser parser) {
      addTo(right, parser, Functions.identity());
      return this;
    }

    /**
     * Adds a right-associative operator {@code parser}. Evaluates the optional
     * {@code action} with the parsed {@code left} term, {@code operator}, and
     * {@code right} term.
     */
    public <T, R> ExpressionGroup right(Parser parser, Function<T, R> action) {
      addTo(right, parser, action);
      return this;
    }

    private Parser buildRight(Parser inner) {
      if (right.isEmpty()) {
        return inner;
      } else {
        Parser sequence = inner.separatedBy(buildChoice(right));
        return sequence.map(new Function<List<Object>, Object>() {
          @Override
          public Object apply(List<Object> sequence) {
            Object result = sequence.get(sequence.size() - 1);
            for (int i = sequence.size() - 2; i > 0; i -= 2) {
              ExpressionResult expressionResult = (ExpressionResult) sequence.get(i);
              result = expressionResult.action.apply(Arrays.asList(sequence.get(i - 1),
                  expressionResult.operator, result));
            }
            return result;
          }
        });
      }
    }

    private final List<Parser> right = new ArrayList<>();

    /**
     * Adds a left-associative operator {@code parser}.
     */
    public ExpressionGroup left(Parser parser) {
      addTo(left, parser, Functions.identity());
      return this;
    }

    /**
     * Adds a left-associative operator {@code parser}. Evaluates the optional
     * {@code action} with the parsed {@code left} term, {@code operator}, and
     * {@code right} term.
     */
    public <T, R> ExpressionGroup left(Parser parser, Function<T, R> action) {
      addTo(left, parser, action);
      return this;
    }

    private Parser buildLeft(Parser inner) {
      if (left.isEmpty()) {
        return inner;
      } else {
        Parser sequence = inner.separatedBy(buildChoice(left));
        return sequence.map(new Function<List<Object>, Object>() {
          @Override
          public Object apply(List<Object> sequence) {
            Object result = sequence.get(0);
            for (int i = 1; i < sequence.size(); i += 2) {
              ExpressionResult expressionResult = (ExpressionResult) sequence.get(i);
              result = expressionResult.action.apply(Arrays.asList(result,
                  expressionResult.operator, sequence.get(i + 1)));
            }
            return result;
          }
        });
      }
    }

    private final List<Parser> left = new ArrayList<>();

    // helper to connect operator parser and action, and add to list
    private <T, R> void addTo(List<Parser> list, Parser parser, final Function<T, R> action) {
      list.add(parser.map(new Function<Object, ExpressionResult>() {
        @Override
        @SuppressWarnings("unchecked")
        public ExpressionResult apply(Object operator) {
          return new ExpressionResult(operator, (Function<Object, Object>) action);
        }
      }));
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
      return buildLeft(buildRight(buildPostfix(buildPrefix(buildPrimitive(inner)))));
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
