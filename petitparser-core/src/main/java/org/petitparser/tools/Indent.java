package org.petitparser.tools;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ChoiceParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.utils.FailureJoiner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Stateful set of parsers to handle indentation-based grammars.
 *
 * <p>Typical use combines {@link #same()} to match the indentation of items at the current
 * level and {@link #during(Parser)} to scope indented blocks:
 *
 * <pre>{@code
 * Indent indent = new Indent();
 * Parser line = indent.same().seq(word().plus().flatten());
 * Parser block = indent.during(line.plus());
 * }</pre>
 */
public class Indent {

  private final Parser parser;
  private final String message;

  final List<String> stack = new ArrayList<>();
  String current = "";

  private final Parser sameParser;
  private final Parser increaseParser;
  private final Parser decreaseParser;

  /**
   * Creates an indentation handler with default whitespace parser and error message.
   */
  public Indent() {
    this(CharacterParser.pattern(" \t"), "indented expected");
  }

  /**
   * Creates an indentation handler with a custom indentation {@code parser}.
   *
   * @param parser the parser used to read a single indentation character or sequence.
   */
  public Indent(Parser parser) {
    this(parser, "indented expected");
  }

  /**
   * Creates an indentation handler with a default whitespace parser and custom error {@code message}.
   *
   * @param message the error message to use when an indentation is expected.
   */
  public Indent(String message) {
    this(CharacterParser.pattern(" \t"), message);
  }

  /**
   * Creates an indentation handler with a custom indentation {@code parser} and error {@code message}.
   *
   * @param parser the parser used to read a single indentation character or sequence.
   * @param message the error message to use when an indentation is expected.
   */
  public Indent(Parser parser, String message) {
    this.parser = parser != null ? parser : CharacterParser.pattern(" \t");
    this.message = message != null ? message : "indented expected";

    this.increaseParser = this.parser
        .plusString(this.message)
        .where((Predicate<String>) value -> {
          if (value.startsWith(current) && value.length() > current.length()) {
            stack.add(current);
            current = value;
            return true;
          } else {
            return false;
          }
        })
        .and();

    this.sameParser = this.parser
        .starString(this.message)
        .where((Predicate<String>) value -> Objects.equals(value, current));

    this.decreaseParser = Parser.epsilon().where((Predicate<Object>) ignored -> {
      if (!stack.isEmpty()) {
        current = stack.remove(stack.size() - 1);
        return true;
      } else {
        return false;
      }
    });
  }

  /**
   * Returns the parser used to read a single indentation step.
   */
  public Parser getParser() {
    return parser;
  }

  /**
   * Returns the error message to use when an indentation is expected.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the stack of parent indentations.
   */
  public List<String> getStack() {
    return stack;
  }

  /**
   * Returns the currently active indentation string.
   */
  public String getCurrent() {
    return current;
  }

  /**
   * Parser that consumes and matches the current indentation level.
   */
  public Parser same() {
    return sameParser;
  }

  /**
   * Parser that increases the indentation.
   *
   * <p>The parser performs the following actions in sequence:
   * <ol>
   *   <li>verifies that the new indentation is deeper than the previous one,</li>
   *   <li>pushes the previous indentation to the stack,</li>
   *   <li>updates the current indentation with the new one, and</li>
   *   <li>returns the new indentation without consuming it.</li>
   * </ol>
   *
   * <p>Prefer using {@link #during(Parser)} instead to properly track the indentation state
   * and ensure rollback on parse failure.
   */
  public Parser increase() {
    return increaseParser;
  }

  /**
   * Parser that decreases the indentation by one level.
   *
   * <p>Prefer using {@link #during(Parser)} instead to properly track the indentation state
   * and ensure rollback on parse failure.
   */
  public Parser decrease() {
    return decreaseParser;
  }

  /**
   * Runs {@code inner} in a deeper indentation scope.
   *
   * <p>Before running {@code inner}, verifies that the next line has a deeper indentation
   * than the current level and pushes the previous level onto the indentation stack.
   * After {@code inner} succeeds, pops and restores the previous indentation level.
   *
   * <p>If {@code inner} fails, the indentation state is automatically rolled back to its state
   * before entering the block, preserving the original parse failure position and message.
   * This ensures that subsequent branches in choice combinators can backtrack safely without
   * state corruption.
   *
   * @param inner the parser to run in an indented block.
   * @return a parser that scopes indentation around {@code inner}.
   */
  public Parser during(Parser inner) {
    Objects.requireNonNull(inner, "Undefined inner parser");
    Parser rollback = Parser.failure().skip(decrease(), null);
    Parser choice = new ChoiceParser(new FailureJoiner.SelectFirst(), inner, rollback);
    return choice.skip(increase(), decrease());
  }

  /**
   * Resets the indentation state to its initial empty stack and current indent.
   */
  public void reset() {
    stack.clear();
    current = "";
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + parser + "]";
  }
}
