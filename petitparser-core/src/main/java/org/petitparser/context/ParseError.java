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

  public Failure getFailure() {
    return failure;
  }
}
