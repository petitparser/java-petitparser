package org.petitparser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * Generic interface of all parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 * @param <T> return type produced by this parser
 */
public interface Parser<T> {

  /**
   * Apply the parser on the given {@code context}.
   */
  Result<T> parse(Context context);

}
