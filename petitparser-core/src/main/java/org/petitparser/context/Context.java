package org.petitparser.context;

/**
 * An immutable parse context.
 */
public class Context<T> {

  /**
   * The input buffer.
   */
  protected final String buffer;

  /**
   * The current position.
   */
  protected final int position;


  protected final T userContext;


  public Context(String buffer, int position, T userContext) {
    this.buffer = buffer;
    this.position = position;
    this.userContext = userContext;
  }

  /**
   * Constructs an immutable parse context.
   *
   * @param buffer the buffer this context is using
   * @param position the position this context is pointing at
   */
//  public Context(String buffer, int position) {
//    this(buffer, position, null);
//  }

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

  public T getUserContext() {
    return userContext;
  }

  /**
   * Returns a successful parse result at the current position.
   *
   * @param value the value of the parse result
   */
  public Success<T> success(Object value) {
    return success(value, position);
  }

  /**
   * Returns a successful parse result.
   *
   * @param value the value of the parse result
   * @param position the position of the parse result
   */
  public Success<T> success(Object value, int position) {
    return new Success<>(buffer, position, userContext, value);
  }

  /**
   * Returns a parse failure at the current position.
   *
   * @param message the error message of the parse result
   */
  public Failure<T> failure(String message) {
    return failure(message, position);
  }

  /**
   * Returns a successful parse result.
   *
   * @param message the error message of the parse result
   * @param position the position of the parse result
   */
  public Failure<T> failure(String message, int position) {
    return new Failure<>(buffer, position, userContext, message);
  }

  @Override
  public String toString() {
    int[] tuple = Token.lineAndColumnOf(buffer, position);
    return getClass().getSimpleName() + "[" + tuple[0] + ":" + tuple[1] + "]";
  }
}
