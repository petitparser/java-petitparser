package org.petitparser.context;

import org.petitparser.buffer.Buffer;

/**
 * Abstract parse context.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Context {

  private final Buffer buffer;
  private final int position;

  /**
   * Constructs an immutable parse context at the default position.
   *
   * @param buffer the buffer this context is using
   */
  public Context(Buffer buffer) {
    this(buffer, 0);
  }

  /**
   * Constructs an immutable parse context.
   *
   * @param buffer the buffer this context is using
   * @param position the position this context is pointing at
   */
  public Context(Buffer buffer, int position) {
    this.buffer = buffer;
    this.position = position;
  }

  @Override
  public String toString() {
    return "Context[" + getPosition() + "]";
  }

  /**
   * Returns the input buffer.
   */
  public Buffer getBuffer() {
    return buffer;
  }

  /**
   * Returns the current position.
   */
  public int getPosition() {
    return position;
  }

  /**
   * Tests if the receiver is a successful parse.
   */
  public boolean isSuccess() {
    return false;
  }

  /**
   * Tests if the receiver is a failure.
   */
  public boolean isFailure() {
    return false;
  }

  /**
   * Returns a successful parse result at the current position.
   *
   * @param value the value of the parse result
   */
  public Success success(Object value) {
    return success(value, getPosition());
  }

  /**
   * Returns a successful parse result.
   *
   * @param value the value of the parse result
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
   * @param message the error message of the parse result
   * @param position the position of the parse result
   */
  public Failure failure(String message, int position) {
    return new Failure(buffer, position, message);
  }

}
