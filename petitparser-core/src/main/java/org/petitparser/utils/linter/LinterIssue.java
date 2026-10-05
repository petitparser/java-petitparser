package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * Encapsulates a single issue identified by a {@link LinterRule}.
 */
public class LinterIssue {

  private final LinterRule rule;
  private final Parser parser;
  private final String description;

  /**
   * Constructs a new linter issue.
   *
   * @param rule        the rule that identified this issue
   * @param parser      the parser object with the issue
   * @param description detailed explanation of the issue
   */
  public LinterIssue(LinterRule rule, Parser parser, String description) {
    this.rule = Objects.requireNonNull(rule, "Undefined rule");
    this.parser = Objects.requireNonNull(parser, "Undefined parser");
    this.description = Objects.requireNonNull(description, "Undefined description");
  }

  /**
   * Returns the rule that identified the issue.
   *
   * @return the rule
   */
  public LinterRule getRule() {
    return rule;
  }

  /**
   * Returns the parser object with the issue.
   *
   * @return the parser
   */
  public Parser getParser() {
    return parser;
  }

  /**
   * Returns the detailed description of the issue.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the severity type of the issue.
   *
   * @return the severity type
   */
  public LinterType getType() {
    return rule.getType();
  }

  /**
   * Returns the title of the rule that reported this issue.
   *
   * @return the title
   */
  public String getTitle() {
    return rule.getTitle();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof LinterIssue)) {
      return false;
    }
    LinterIssue other = (LinterIssue) obj;
    return Objects.equals(rule, other.rule) &&
        Objects.equals(parser, other.parser) &&
        Objects.equals(description, other.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rule, parser, description);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(type: " + getType() + ", title: " + getTitle() +
        ", parser: " + parser + ", description: " + description + ")";
  }
}
