package org.petitparser.utils;

import org.petitparser.parser.Parser;
import org.petitparser.utils.linter.CharacterRepeaterRule;
import org.petitparser.utils.linter.DuplicateParserRule;
import org.petitparser.utils.linter.LeftRecursionRule;
import org.petitparser.utils.linter.LinterIssue;
import org.petitparser.utils.linter.LinterRule;
import org.petitparser.utils.linter.LinterType;
import org.petitparser.utils.linter.NestedChoiceRule;
import org.petitparser.utils.linter.NullableRepeaterRule;
import org.petitparser.utils.linter.OverlappingChoiceRule;
import org.petitparser.utils.linter.RepeatedChoiceRule;
import org.petitparser.utils.linter.UnnecessaryFlattenRule;
import org.petitparser.utils.linter.UnnecessaryResolvableRule;
import org.petitparser.utils.linter.UnoptimizedFlattenRule;
import org.petitparser.utils.linter.UnreachableChoiceRule;
import org.petitparser.utils.linter.UnresolvedSettableRule;
import org.petitparser.utils.linter.UnusedResultRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Grammar linter engine executing lint rules over parser graphs.
 */
public class Linter {

  private Linter() {}

  /**
   * All 13 default linter rules.
   */
  public static final List<LinterRule> ALL_RULES = List.of(
      new CharacterRepeaterRule(),
      new DuplicateParserRule(),
      new LeftRecursionRule(),
      new NestedChoiceRule(),
      new NullableRepeaterRule(),
      new OverlappingChoiceRule(),
      new RepeatedChoiceRule(),
      new UnnecessaryFlattenRule(),
      new UnnecessaryResolvableRule(),
      new UnoptimizedFlattenRule(),
      new UnreachableChoiceRule(),
      new UnresolvedSettableRule(),
      new UnusedResultRule()
  );

  /**
   * Default excluded types (rules of {@link LinterType#INFO} are ignored by default).
   */
  public static final Set<LinterType> DEFAULT_EXCLUDED_TYPES = Collections.singleton(LinterType.INFO);

  /**
   * Lints the grammar rooted at {@code parser} using default rules.
   *
   * @param parser the root parser
   * @return list of identified issues
   */
  public static List<LinterIssue> lint(Parser parser) {
    return lint(parser, null, null, Collections.emptySet(), DEFAULT_EXCLUDED_TYPES);
  }

  /**
   * Lints the grammar rooted at {@code parser} using custom rules.
   *
   * @param parser the root parser
   * @param rules  custom rules to execute
   * @return list of identified issues
   */
  public static List<LinterIssue> lint(Parser parser, List<LinterRule> rules) {
    return lint(parser, null, rules, Collections.emptySet(), Collections.emptySet());
  }

  /**
   * Lints the grammar rooted at {@code parser} using custom rules.
   *
   * @param parser the root parser
   * @param rules  custom rules to execute
   * @return list of identified issues
   */
  public static List<LinterIssue> lint(Parser parser, LinterRule... rules) {
    return lint(parser, Arrays.asList(rules));
  }

  /**
   * Lints the grammar rooted at {@code parser} with an issue callback.
   *
   * @param parser   the root parser
   * @param callback consumer called on each issue
   * @return list of identified issues
   */
  public static List<LinterIssue> lint(Parser parser, Consumer<LinterIssue> callback) {
    return lint(parser, callback, null, Collections.emptySet(), DEFAULT_EXCLUDED_TYPES);
  }

  /**
   * Lints the grammar rooted at {@code parser} with an issue callback and custom rules.
   *
   * @param parser   the root parser
   * @param callback consumer called on each issue
   * @param rules    custom rules to execute
   * @return list of identified issues
   */
  public static List<LinterIssue> lint(
      Parser parser, Consumer<LinterIssue> callback, List<LinterRule> rules) {
    return lint(parser, callback, rules, Collections.emptySet(), Collections.emptySet());
  }

  /**
   * Fully configurable lint execution.
   *
   * @param parser        the root parser
   * @param callback      optional consumer called on each issue
   * @param rules         custom rules, or null to use {@link #ALL_RULES}
   * @param excludedRules set of rule titles to exclude when using default rules
   * @param excludedTypes set of issue types to exclude when using default rules
   * @return list of identified issues
   */
  public static List<LinterIssue> lint(
      Parser parser,
      Consumer<LinterIssue> callback,
      List<LinterRule> rules,
      Set<String> excludedRules,
      Set<LinterType> excludedTypes) {
    Objects.requireNonNull(parser, "Undefined parser");
    List<LinterIssue> issues = new ArrayList<>();
    Analyzer analyzer = Analyzer.of(parser);
    List<LinterRule> selectedRules;
    if (rules != null) {
      selectedRules = rules;
    } else {
      Set<String> excRules = excludedRules != null ? excludedRules : Collections.emptySet();
      Set<LinterType> excTypes = excludedTypes != null ? excludedTypes : DEFAULT_EXCLUDED_TYPES;
      selectedRules = ALL_RULES.stream()
          .filter(rule -> !excRules.contains(rule.getTitle()) && !excTypes.contains(rule.getType()))
          .collect(Collectors.toList());
    }

    for (Parser current : analyzer.parsers()) {
      for (LinterRule rule : selectedRules) {
        rule.run(analyzer, current, issue -> {
          if (callback != null) {
            callback.accept(issue);
          }
          issues.add(issue);
        });
      }
    }
    return Collections.unmodifiableList(issues);
  }

  /**
   * Creates a fluent query builder to configure and run the linter.
   *
   * @param parser the root parser
   * @return a new query builder
   */
  public static Query query(Parser parser) {
    return new Query(parser);
  }

  /**
   * Fluent configuration query for linting.
   */
  public static class Query {
    private final Parser parser;
    private Consumer<LinterIssue> callback;
    private List<LinterRule> rules;
    private final Set<String> excludedRules = new HashSet<>();
    private final Set<LinterType> excludedTypes = new HashSet<>(DEFAULT_EXCLUDED_TYPES);

    public Query(Parser parser) {
      this.parser = Objects.requireNonNull(parser, "Undefined parser");
    }

    public Query onIssue(Consumer<LinterIssue> callback) {
      this.callback = callback;
      return this;
    }

    public Query rules(List<LinterRule> rules) {
      this.rules = rules;
      return this;
    }

    public Query rules(LinterRule... rules) {
      this.rules = Arrays.asList(rules);
      return this;
    }

    public Query excludeRule(String ruleTitle) {
      this.excludedRules.add(ruleTitle);
      return this;
    }

    public Query excludeRules(Collection<String> ruleTitles) {
      this.excludedRules.addAll(ruleTitles);
      return this;
    }

    public Query excludeType(LinterType type) {
      this.excludedTypes.add(type);
      return this;
    }

    public Query excludeTypes(Collection<LinterType> types) {
      this.excludedTypes.addAll(types);
      return this;
    }

    public Query includeAllTypes() {
      this.excludedTypes.clear();
      return this;
    }

    public List<LinterIssue> lint() {
      return Linter.lint(parser, callback, rules, excludedRules, excludedTypes);
    }
  }
}
