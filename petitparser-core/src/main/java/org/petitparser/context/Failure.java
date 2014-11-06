package org.petitparser.context;

import org.petitparser.buffer.Buffer;

/**
 * An immutable parse failure.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Failure extends Result {

  private final String message;

  Failure(Buffer buffer, int position, String message) {
    super(buffer, position);
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
    return "Failure[" + getPosition() + "]: " + getMessage();
  }

}
