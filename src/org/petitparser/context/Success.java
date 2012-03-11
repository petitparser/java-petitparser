package org.petitparser.context;

import org.petitparser.buffer.Buffer;

/**
 * An immutable parse success.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Success<T> extends Result<T> {

  private final T result;

  Success(Buffer buffer, int position, T result) {
    super(buffer, position);
    this.result = result;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public T get() {
    return result;
  }

  @Override
  public String toString() {
    return "Success[" + getPosition() + "]: " + (get() == null ? "null" : get().toString());
  }

}
