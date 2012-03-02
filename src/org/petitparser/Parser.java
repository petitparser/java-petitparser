package org.petitparser;

import org.petitparser.context.Context;

/**
 * Generic interface of all parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 * @param <T> The return type produced by this parser.
 */
public interface Parser<T> {

  /**
   * Apply the parser on the given {@code context}.
   */
  Context<T> parse(Context<?> context);

}
