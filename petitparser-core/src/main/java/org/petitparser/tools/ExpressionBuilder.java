package org.petitparser.tools;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.combinators.SequenceParser;
import org.petitparser.parser.primitive.FailureParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * A builder that allows the simple definition of expression grammars with prefix, postfix, and
 * left- and right-associative infix operators.
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
    Parser parser =
        FailureParser.withMessage("Highest priority group should define a primitive parser.");
    for (ExpressionGroup group : groups) {
      parser = group.build(parser);
    }
    return parser;
  }

  /**
   * Models a group of operators of the same precedence.
   */
  public static class ExpressionGroup {

    private final List<Parser> primitives = new ArrayList<>();
    private final List<Parser> prefix = new ArrayList<>();
    private final List<Parser> postfix = new ArrayList<>();
    private final List<Parser> right = new ArrayList<>();
    private final List<Parser> left = new ArrayList<>();

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

    /**
     * Adds a prefix operator {@code parser}.
     */
    public ExpressionGroup prefix(Parser parser) {
      addTo(prefix, parser, Function.identity());
      return this;
    }

    /**
     * Adds a prefix operator {@code parser}. Evaluates the optional {@code action} with the parsed
     * {@code operator} and {@code value}.
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
            List<ExpressionResult> tuples = (List<ExpressionResult>) tuple.get(0);
            Collections.reverse(tuples);
            for (ExpressionResult result : tuples) {
              value = result.action.apply(Arrays.asList(result.operator, value));
            }
            return value;
          }
        });
      }
    }

    /**
     * Adds a postfix operator {@code parser}.
     */
    public ExpressionGroup postfix(Parser parser) {
      addTo(postfix, parser, Function.identity());
      return this;
    }

    /**
     * Adds a postfix operator {@code parser}. Evaluates the optional {@code action} with the parsed
     * {@code value} and {@code operator}.
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

    /**
     * Adds a right-associative operator {@code parser}.
     */
    public ExpressionGroup right(Parser parser) {
      addTo(right, parser, Function.identity());
      return this;
    }

    /**
     * Adds a right-associative operator {@code parser}. Evaluates the optional {@code action} with
     * the parsed {@code left} term, {@code operator}, and {@code right} term.
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
              result = expressionResult.action
                  .apply(Arrays.asList(sequence.get(i - 1), expressionResult.operator, result));
            }
            return result;
          }
        });
      }
    }

    /**
     * Adds a left-associative operator {@code parser}.
     */
    public ExpressionGroup left(Parser parser) {
      addTo(left, parser, Function.identity());
      return this;
    }

    /**
     * Adds a left-associative operator {@code parser}. Evaluates the optional {@code action} with
     * the parsed {@code left} term, {@code operator}, and {@code right} term.
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
              result = expressionResult.action
                  .apply(Arrays.asList(result, expressionResult.operator, sequence.get(i + 1)));
            }
            return result;
          }
        });
      }
    }

    // helper to connect operator parser and action, and add to list
    private <T, R> void addTo(List<Parser> list, Parser parser, final Function<T, R> action) {
      list.add(parser.map(operator -> new ExpressionResult(operator, (Function<Object, Object>) action)));
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
