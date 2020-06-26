package org.petitparser.context;

/**
 * An immutable parse success.
 */
public class Success<U> extends Result<U> {

  private final Object result;

  public Success(String buffer, int position, U userContext, Object result) {
    super(buffer, position, userContext);
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
    return super.toString() + ": " + result;
  }
}
