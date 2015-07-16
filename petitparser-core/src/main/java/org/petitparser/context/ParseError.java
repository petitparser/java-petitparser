package org.petitparser.context;

/**
 * A parse error.
 */
public class ParseError extends RuntimeException {

  private final Failure failure;

  public ParseError(Failure failure) {
    super(failure.getMessage());
    this.failure = failure;
  }

  /**
   * Return the parse {@link Context} causing this error.
   */
  public Failure getFailure() {
    return failure;
  }
}
