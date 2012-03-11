package org.petitparser.parser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that silently consumes spaces before and after the delegate parser.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class TrimmingParser extends DelegateParser {

  private final Parser trimmer;

  public TrimmingParser(Parser delegate, Parser trimmer) {
    super(delegate);
    this.trimmer = trimmer;
  }

  @Override
  public Result parse(Context context) {
    Context current = context;
    do {
      current = trimmer.parse(current);
    } while (current.isSuccess());
    Result result = super.parse(current);
    if (result.isFailure()) {
      return result;
    }
    current = result;
    do {
      current = trimmer.parse(current);
    } while (current.isSuccess());
    return current.success(result.get());
  }

}
