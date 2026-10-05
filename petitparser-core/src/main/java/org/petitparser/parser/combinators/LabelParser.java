package org.petitparser.parser.combinators;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * A parser that always defers to its delegate, but that also holds a label for debugging purposes.
 */
public class LabelParser extends DelegateParser {

  protected final String label;

  /**
   * Constructs a label parser wrapping the {@code delegate} with a {@code label}.
   *
   * @param delegate the delegate parser.
   * @param label the label of the parser.
   */
  public LabelParser(Parser delegate, String label) {
    super(delegate);
    this.label = Objects.requireNonNull(label, "Undefined label");
  }

  /**
   * Returns the label of this parser.
   */
  public String getLabel() {
    return label;
  }

  @Override
  public Result parseOn(Context context) {
    return delegate.parseOn(context);
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    return delegate.fastParseOn(buffer, position);
  }

  @Override
  public String toString() {
    return delegate.toString() + "[" + label + "]";
  }

  @Override
  public LabelParser copy() {
    return new LabelParser(delegate, label);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(label, ((LabelParser) other).label);
  }
}
