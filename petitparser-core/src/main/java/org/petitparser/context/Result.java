package org.petitparser.context;

/**
 * An immutable abstract parse result.
 *
 * @param <U> Custom context type
 */
public abstract class Result<U> extends Context<U> {

  public Result(String buffer, int position, U userContext) {
    super(buffer, position, userContext);
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
