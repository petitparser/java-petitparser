package ch.renggli.petitparser;

/**
 * Handles the intermediate parse results.
 *
 * @author renggli@gmail.com (Lukas Renggli)
 */
public interface ParseOutput {

  /**
   * Pushes a parse result into the parser context.
   */
  <T> void push(ParseResult<T> result);

  /**
   * Pops a parse result from the parse context.
   */
  <T> ParseResult<T> pop();

  /**
   * Tests if the top result is an error.
   */
  boolean isError();

}
