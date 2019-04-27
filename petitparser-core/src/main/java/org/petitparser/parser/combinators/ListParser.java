package org.petitparser.parser.combinators;

import org.petitparser.parser.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Abstract parser that parses a list of things in some way (to be specified by
 * the subclasses).
 */
public abstract class ListParser extends Parser {

  protected final Parser[] parsers;

  public ListParser(Parser... parsers) {
    this.parsers = Objects.requireNonNull(parsers, "Undefined parser list");
  }

  @Override
  public void replace(Parser source, Parser target) {
    super.replace(source, target);
    for (int i = 0; i < parsers.length; i++) {
      if (parsers[i] == source) {
        parsers[i] = target;
      }
    }
  }

  @Override
  public List<Parser> getChildren() {
    return Arrays.asList(parsers);
  }
}
