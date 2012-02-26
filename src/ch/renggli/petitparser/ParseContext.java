package ch.renggli.petitparser;

/**
 * The parse context.
 *
 * @author renggli@gmail.com (Lukas Renggli)
 */
public interface ParseContext {

  /**
   * The input state of this parser context.
   */
  ParseInput input();

  /**
   * The output state of this parser context.
   */
  ParseOutput output();

}
