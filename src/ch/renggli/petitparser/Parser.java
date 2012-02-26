package ch.renggli.petitparser;

/**
 * Interface that all parsers implement.
 *
 * @author renggli@gmail.com (Lukas Renggli)
 */
public interface Parser<T> {

  /**
   * Parse from the given {@code context}.
   */
  void parseOn(ParseContext context);




}
