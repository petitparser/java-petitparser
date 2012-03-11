package org.petitparser.context;

import org.petitparser.buffer.Buffer;

/**
 * An immutable and abstract parse result.
 * 
 * @author Lukas Renggli (renggli@gmail.com)
 * @param <T> The type of the parse result.
 */
public abstract class Result<T> extends Context {

  Result(Buffer buffer, int position) {
    super(buffer, position);
  }

  /**
   * Returns the result of this parse context.
   */
  public abstract T get();

  /**
   * Returns the message of this parse context, only set in case of failure.
   */
  public String getMessage() {
    return null;
  }

  /**
   * Unchecked cast from {@code T} to {@code U}.
   */
  @SuppressWarnings("unchecked")
  public <U> Result<U> cast() {
    return (Result<U>) this;
  }

}
