package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.ResolvableParser;
import org.petitparser.utils.Analyzer;

import java.util.function.Consumer;

/**
 * Identifies unresolved resolvable wrappers.
 */
public class UnnecessaryResolvableRule extends LinterRule {

  public UnnecessaryResolvableRule() {
    super(LinterType.WARNING, "Unnecessary resolvable");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof ResolvableParser) {
      callback.accept(new LinterIssue(
          this,
          parser,
          "Resolvable parsers are used during construction of recursive grammars. " +
          "While they typically dispatch to their delegate, they add unnecessary " +
          "overhead and can be avoided by removing them before parsing using `resolve(parser)`."
      ));
    }
  }
}
