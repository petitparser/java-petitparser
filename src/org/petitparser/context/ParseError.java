package org.petitparser.context;

/**
 * A parse error.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class ParseError extends RuntimeException {

  private static final long serialVersionUID = 1865114289785709043L;

  private final Failure context;

  public ParseError(Failure context) {
    super(context.getMessage());
    this.context = context;
  }

  public Failure getContext() {
    return context;
  }

}
