package org.petitparser;

import org.petitparser.context.Context;
import org.petitparser.context.Result;

/**
 * Generic interface of all parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public interface Parsable {

  /**
   * Apply the parser on the given {@code context}.
   */
  Result parse(Context context);

}
