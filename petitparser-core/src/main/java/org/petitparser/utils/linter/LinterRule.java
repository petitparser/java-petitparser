package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.utils.Analyzer;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Encapsulates a single grammar linter rule.
 */
public abstract class LinterRule {

  /**
   * Functional interface for implementing a custom linter rule runner.
   */
  @FunctionalInterface
  public interface LinterFunction {
    void run(LinterRule rule, Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback);
  }

  /**
   * Constructs a custom {@link LinterRule} backed by a lambda function.
   *
   * @param type     the severity type
   * @param title    the human-readable title
   * @param function the function to execute on each parser
   * @return the created rule
   */
  public static LinterRule of(LinterType type, String title, LinterFunction function) {
    Objects.requireNonNull(function, "Undefined function");
    return new LinterRule(type, title) {
      @Override
      public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
        function.run(this, analyzer, parser, callback);
      }
    };
  }

  private final LinterType type;
  private final String title;

  /**
   * Constructs a new linter rule.
   *
   * @param type  the severity type
   * @param title the human-readable title
   */
  protected LinterRule(LinterType type, String title) {
    this.type = Objects.requireNonNull(type, "Undefined type");
    this.title = Objects.requireNonNull(title, "Undefined title");
  }

  /**
   * Returns the severity type of issues detected by this rule.
   *
   * @return the severity type
   */
  public LinterType getType() {
    return type;
  }

  /**
   * Returns the human-readable title of this rule.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Executes this rule using the provided {@code analyzer} on {@code parser}.
   *
   * @param analyzer the analyzer for grammar graph introspection
   * @param parser   the current parser node being inspected
   * @param callback consumer called whenever an issue is detected
   */
  public abstract void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback);

  /**
   * Formats an iterable into a bulleted or numbered list.
   *
   * @param objects the items to format
   * @param offset  optional starting index for numbered list, or null for bulleted
   * @return the formatted string
   */
  public static String formatIterable(Iterable<?> objects, Integer offset) {
    StringBuilder buffer = new StringBuilder();
    int i = 0;
    for (Object object : objects) {
      if (i > 0) {
        buffer.append('\n');
      }
      if (offset != null) {
        buffer.append(' ').append(offset + i).append(": ");
      } else {
        buffer.append(" - ");
      }
      buffer.append(object);
      i++;
    }
    return buffer.toString();
  }

  /**
   * Formats an iterable into a bulleted list.
   *
   * @param objects the items to format
   * @return the formatted string
   */
  public static String formatIterable(Iterable<?> objects) {
    return formatIterable(objects, null);
  }

  /**
   * Tests whether two iterables of parsers are structurally equal under {@link Parser#isEqualTo(Parser)}.
   *
   * @param first  the first collection
   * @param second the second collection
   * @return true if all elements match pairwise structurally
   */
  public static boolean isParserIterableEqual(Iterable<Parser> first, Iterable<Parser> second) {
    for (Parser one : first) {
      boolean found = false;
      for (Parser two : second) {
        if (one.isEqualTo(two)) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    for (Parser two : second) {
      boolean found = false;
      for (Parser one : first) {
        if (two.isEqualTo(one)) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    LinterRule other = (LinterRule) obj;
    return type == other.type && Objects.equals(title, other.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass(), type, title);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(type: " + type + ", title: " + title + ")";
  }
}
