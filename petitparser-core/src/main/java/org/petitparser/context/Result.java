package org.petitparser.context;

/**
 * An immutable abstract parse result.
 */
public abstract class Result extends Context {

  public Result(String buffer, int position) {
    super(buffer, position);
  }

  /**
   * Returns {@code true} if this result indicates a parse success.
   */
  public boolean isSuccess() {
    return false;
  }

  /**
   * Returns {@code true} if this result indicates a parse failure.
   */
  public boolean isFailure() {
    return false;
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
