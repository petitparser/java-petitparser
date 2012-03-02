package org.petitparser.context;

import org.petitparser.buffer.Buffer;

/**
 * Represents a parse failure.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class FailureContext<T> extends Context<T> {

  private final String message;

  public FailureContext(Buffer buffer, int position, String message) {
    super(buffer, position);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public boolean isFailure() {
    return true;
  }

  @Override
  public T get() {
    throw new IllegalStateException(getMessage());
  }

}
