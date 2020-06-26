package org.petitparser.context;

/**
 * An immutable parse failure.
 */
public class Failure<U> extends Result<U> {

  private final String message;

  public Failure(String buffer, int position, U userContext, String message) {
    super(buffer, position, userContext);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public boolean isFailure() {
    return true;
  }

  @Override
  public <T> T get() {
    throw new ParseError(this);
  }

  @Override
  public String toString() {
    return super.toString() + ": " + message;
  }
}
