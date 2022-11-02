package org.petitparser.context;

/**
 * An immutable parse context.
 */
public class Context {

  /**
   * The input buffer.
   */
  protected final String buffer;

  /**
   * The current position.
   */
  protected final int position;

  /**
   * Constructs an immutable parse context.
   *
   * @param buffer   the buffer this context is using
   * @param position the position this context is pointing at
   */
  public Context(String buffer, int position) {
    this.buffer = buffer;
    this.position = position;
  }

  /**
   * Returns the input buffer.
   */
  public String getBuffer() {
    return buffer;
  }

  /**
   * Returns the current position.
   */
  public int getPosition() {
    return position;
  }

  /**
   * Returns a successful parse result at the current position.
   *
   * @param value the value of the parse result
   */
  public Success success(Object value) {
    return success(value, position);
  }

  /**
   * Returns a successful parse result.
   *
   * @param value    the value of the parse result
   * @param position the position of the parse result
   */
  public Success success(Object value, int position) {
    return new Success(buffer, position, value);
  }

  /**
   * Returns a parse failure at the current position.
   *
   * @param message the error message of the parse result
   */
  public Failure failure(String message) {
    return failure(message, position);
  }

  /**
   * Returns a successful parse result.
   *
   * @param message  the error message of the parse result
   * @param position the position of the parse result
   */
  public Failure failure(String message, int position) {
    return new Failure(buffer, position, message);
  }

  @Override
  public String toString() {
    int[] tuple = Token.lineAndColumnOf(buffer, position);
    return getClass().getSimpleName() + "[" + tuple[0] + ":" + tuple[1] + "]";
  }
}
