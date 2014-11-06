package org.petitparser.context;

import org.petitparser.buffer.Buffer;

/**
 * An immutable and abstract parse result.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class Result extends Context {

  Result(Buffer buffer, int position) {
    super(buffer, position);
  }

  /**
   * Returns the result of this parse context.
   *
   * @param <T> The type of the parse result.
   */
  public abstract <T> T get();

  /**
   * Returns the message of this parse context, only set in case of failure.
   */
  public String getMessage() {
    return null;
  }

}
