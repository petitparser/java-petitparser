package org.petitparser.context;

import org.petitparser.buffer.Buffer;

/**
 * An immutable parse success.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Success extends Result {

  private final Object result;

  Success(Buffer buffer, int position, Object result) {
    super(buffer, position);
    this.result = result;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T get() {
    return (T) result;
  }

  @Override
  public String toString() {
    String message = get() == null ? "null" : get().toString();
    return "Success[" + getPosition() + "]: " + message;
  }

}
