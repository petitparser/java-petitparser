package org.petitparser;

/**
 * The parse context with input and output state.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public interface ParseContext<I, O> extends ParseState<ParseContext<I, O>> {

  /**
   * The input state of this parse.
   */
  ParseInput<I> input();

  /**
   * The output state of this parse.
   */
  ParseOutput<O> output();

}
