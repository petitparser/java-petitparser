package org.petitparser.utils.linter;

import org.petitparser.parser.Parser;
import org.petitparser.parser.actions.FlattenParser;
import org.petitparser.utils.Analyzer;

import java.util.function.Consumer;

/**
 * Identifies {@code flatten()} parsers without an error message preventing switch to fast parse mode.
 */
public class UnoptimizedFlattenRule extends LinterRule {

  public UnoptimizedFlattenRule() {
    super(LinterType.INFO, "Unoptimized flatten");
  }

  @Override
  public void run(Analyzer analyzer, Parser parser, Consumer<LinterIssue> callback) {
    if (parser instanceof FlattenParser && ((FlattenParser) parser).getMessage() == null) {
      callback.accept(new LinterIssue(
          this,
          parser,
          "A flatten parser without an error message is unable to switch " +
          "to the fast parsing mode. This can lead to inefficient parsers " +
          "and can usually easily fixed by providing an error message " +
          "that should be used in case the delegate fails to parse."
      ));
    }
  }
}
