package org.petitparser.parser.repeating;

import org.petitparser.parser.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * An abstract parser that repeatedly parses between 'min' and 'max' instances of its delegate and
 * that requires the input to be completed with a specified parser 'limit'. Subclasses provide
 * repeating behavior as typically seen in regular expression implementations (non-blind).
 */
public abstract class LimitedRepeatingParser extends RepeatingParser {

  protected Parser limit;

  public LimitedRepeatingParser(Parser delegate, Parser limit, int min, int max) {
    super(delegate, min, max);
    this.limit = Objects.requireNonNull(limit);
  }

  @Override
  public List<Parser> getChildren() {
    return Arrays.asList(delegate, limit);
  }

  @Override
  public void replace(Parser source, Parser target) {
    super.replace(source, target);
    if (limit == source) {
      limit = target;
    }
  }
}
