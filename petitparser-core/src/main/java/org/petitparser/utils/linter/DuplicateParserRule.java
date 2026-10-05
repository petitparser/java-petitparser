package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.utils.Analyzer;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Identifies duplicate structurally equal parser instances in grammar graph.
 */
public class DuplicateParserRule extends LinterRule {

  public DuplicateParserRule() {
    super(LinterType.INFO, "Duplicate parser");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    List<Parser> duplicates = analyzer.parsers().stream()
        .filter(parser::isEqualTo)
        .collect(Collectors.toList());
    if (duplicates.size() > 1 && duplicates.get(0) == parser) {
      callback.accept(new LinterIssue(
          this,
          parser,
          duplicates.size() + " instances of the same parser exist in this grammar. " +
          "If possible, reuse the same parser instances to reduce memory footprint and increase performance."
      ));
    }
  }
}
