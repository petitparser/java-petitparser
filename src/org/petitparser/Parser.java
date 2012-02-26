package org.petitparser;

/**
 * Generic interface of all parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 * @param <I> input type of the parser.
 * @param <O> output type of the parser.
 */
public interface Parser<I, O> {

  /**
   * Parse from the given {@code context}.
   */
  void parse(ParseContext<I, O> context);

}
