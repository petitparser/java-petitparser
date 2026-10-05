package org.petitparser.parser.repeating;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.SequentialParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A parser that repeatedly parses between 'min' and 'max' instances of its delegate
 * separated by a separator parser, returning a {@link SeparatedList}.
 */
public class SeparatedRepeatingParser extends RepeatingParser implements SequentialParser {

  protected Parser separator;

  /**
   * Constructs a separated repeating parser.
   *
   * @param delegate the parser matching the elements.
   * @param separator the parser matching the separator.
   * @param min the minimum number of elements.
   * @param max the maximum number of elements, or {@link RepeatingParser#UNBOUNDED}.
   */
  public SeparatedRepeatingParser(
      Parser delegate, Parser separator, int min, int max) {
    super(delegate, min, max);
    this.separator = Objects.requireNonNull(separator, "Undefined separator");
  }

  public Parser getSeparator() {
    return separator;
  }

  public void setSeparator(Parser separator) {
    this.separator = Objects.requireNonNull(separator, "Undefined separator");
  }

  @Override
  public Result parseOn(Context context) {
    Context current = context;
    List<Object> elements = new ArrayList<>();
    List<Object> separators = new ArrayList<>();

    while (elements.size() < min) {
      if (!elements.isEmpty()) {
        Result separation = separator.parseOn(current);
        if (separation.isFailure()) {
          return separation;
        }
        current = separation;
        separators.add(separation.get());
      }
      Result result = delegate.parseOn(current);
      if (result.isFailure()) {
        return result;
      }
      current = result;
      elements.add(result.get());
    }

    while (max == UNBOUNDED || elements.size() < max) {
      Context previous = current;
      if (!elements.isEmpty()) {
        Result separation = separator.parseOn(current);
        if (separation.isFailure()) {
          break;
        }
        current = separation;
        separators.add(separation.get());
      }
      Result result = delegate.parseOn(current);
      if (result.isFailure()) {
        if (!elements.isEmpty()) {
          separators.remove(separators.size() - 1);
        }
        return previous.success(new SeparatedList<>(elements, separators));
      }
      current = result;
      elements.add(result.get());
    }

    return current.success(new SeparatedList<>(elements, separators));
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    int count = 0;
    int current = position;

    while (count < min) {
      if (count > 0) {
        int separation = separator.fastParseOn(buffer, current);
        if (separation < 0) {
          return -1;
        }
        current = separation;
      }
      int result = delegate.fastParseOn(buffer, current);
      if (result < 0) {
        return -1;
      }
      count++;
      current = result;
    }

    while (max == UNBOUNDED || count < max) {
      int previous = current;
      if (count > 0) {
        int separation = separator.fastParseOn(buffer, current);
        if (separation < 0) {
          break;
        }
        current = separation;
      }
      int result = delegate.fastParseOn(buffer, current);
      if (result < 0) {
        return previous;
      }
      count++;
      current = result;
    }

    return current;
  }

  @Override
  public List<Parser> getChildren() {
    return Arrays.asList(delegate, separator);
  }

  @Override
  public void replace(Parser source, Parser target) {
    super.replace(source, target);
    if (separator == source) {
      separator = target;
    }
  }

  @Override
  public SeparatedRepeatingParser copy() {
    return new SeparatedRepeatingParser(delegate, separator, min, max);
  }
}
