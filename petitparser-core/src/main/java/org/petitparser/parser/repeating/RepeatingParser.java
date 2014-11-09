package org.petitparser.parser.repeating;

import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.Objects;

/**
 * An abstract parser that repeatedly parses between 'min' and 'max' instances of its delegate.
 */
public abstract class RepeatingParser extends DelegateParser {

  public static final int UNBOUNDED = Integer.MAX_VALUE;

  protected final int min;
  protected final int max;

  public RepeatingParser(Parser delegate, int min, int max) {
    super(delegate);
    this.min = min;
    this.max = max;
    if (min < 0 || max < min) {
      throw new IllegalStateException("Invalid repetition count: " + min + ".." + max);
    }
  }

  @Override
  public String toString() {
    String lower = Integer.toString(min);
    String upper = max == UNBOUNDED ? "*" : Integer.toString(max);
    return super.toString() + "[" + lower + ".." + upper + "]";
  }

  @Override
  public boolean equalsProperties(Parser other) {
    return super.equalsProperties(other) &&
        Objects.equals(min, ((RepeatingParser) other).min) &&
        Objects.equals(max, ((RepeatingParser) other).max);
  }
}
