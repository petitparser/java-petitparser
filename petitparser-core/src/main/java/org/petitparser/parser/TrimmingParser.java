package org.petitparser.parser;

import java.util.List;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * A parser that silently consumes spaces before and after the delegate parser.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class TrimmingParser extends DelegateParser {

  private Parser trimmer;

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

  @Override
  public void replace(Parser source, Parser target) {
    super.replace(source, target);
    if (trimmer == source) {
      trimmer = target;
    }
  }

  @Override
  public List<Parser> getChildren() {
    List<Parser> children = super.getChildren();
    children.add(trimmer);
    return children;
  }

}
