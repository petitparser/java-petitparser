package org.petitparser.utils;

import org.petitparser.parser.Parser;
import org.petitparser.utils.linter.LinterIssue;
import org.petitparser.utils.linter.LinterRule;
import org.petitparser.utils.linter.LinterType;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Convenience entry point for grammar linting in {@code org.petitparser.utils}.
 */
public final class Linter {

  private Linter() {}

  public static List<LinterIssue> lint(Parser parser) {
    return org.petitparser.utils.linter.Linter.lint(parser);
  }

  public static List<LinterIssue> lint(Parser parser, List<LinterRule> rules) {
    return org.petitparser.utils.linter.Linter.lint(parser, rules);
  }

  public static List<LinterIssue> lint(Parser parser, LinterRule... rules) {
    return org.petitparser.utils.linter.Linter.lint(parser, rules);
  }

  public static List<LinterIssue> lint(Parser parser, Consumer<LinterIssue> callback) {
    return org.petitparser.utils.linter.Linter.lint(parser, callback);
  }

  public static List<LinterIssue> lint(
      Parser parser, Consumer<LinterIssue> callback, List<LinterRule> rules) {
    return org.petitparser.utils.linter.Linter.lint(parser, callback, rules);
  }

  public static List<LinterIssue> lint(
      Parser parser,
      Consumer<LinterIssue> callback,
      List<LinterRule> rules,
      Set<String> excludedRules,
      Set<LinterType> excludedTypes) {
    return org.petitparser.utils.linter.Linter.lint(
        parser, callback, rules, excludedRules, excludedTypes);
  }

  public static org.petitparser.utils.linter.Linter.Query query(Parser parser) {
    return org.petitparser.utils.linter.Linter.query(parser);
  }
}
