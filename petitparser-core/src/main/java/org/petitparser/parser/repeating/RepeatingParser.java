package org.petitparser.parser.repeating;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.Objects;

/**
 * An abstract parser that repeatedly parses between 'min' and 'max' instances of its delegate.
 */
public abstract class RepeatingParser extends DelegateParser {

  public static final int UNBOUNDED = -1;

  protected final int min;
  protected final int max;

  public RepeatingParser(Parser delegate, int min, int max) {
    super(delegate);
    this.min = min;
    this.max = max;
    if (min < 0) {
      throw new IllegalArgumentException("Negative min repetitions");
    }
    if (max != UNBOUNDED && min > max) {
      throw new IllegalArgumentException("Invalid max repetitions");
    }
  }

  @Override
  public boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(min, ((RepeatingParser) other).min) &&
        Objects.equals(max, ((RepeatingParser) other).max);
  }

  @Override
  public String toString() {
    return super.toString() + "[" + min + ".." + (max == UNBOUNDED ? "*" : max) + "]";
  }
}
