package org.petitparser.context;

import org.petitparser.buffer.Buffer;

/**
 * Represents a parse success.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SuccessContext<T> extends Context<T> {

  private final T result;

  public SuccessContext(Buffer buffer, int position, T result) {
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

}
