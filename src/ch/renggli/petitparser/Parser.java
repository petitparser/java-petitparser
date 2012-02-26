package ch.renggli.petitparser;

/**
 * Generic interface of all parsers.
 *
 * @author renggli@gmail.com (Lukas Renggli)
 * @param <I> type of the input elements.
 * @param <S> type of the input state.
 * @param <O> result type of the parser.
 */
public interface Parser<I, S, O> {

  /**
   * Parse from the given {@code context}.
   */
  void parseOn(ParseContext context);

}
