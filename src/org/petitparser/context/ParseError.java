package org.petitparser.context;

import org.petitparser.utils.FailureContext;


public class ParseError extends RuntimeException {

  private static final long serialVersionUID = 1865114289785709043L;

  private final FailureContext<?> context;

  public ParseError(FailureContext<?> context) {
    super(context.getMessage());
    this.context = context;
  }

  @SuppressWarnings("unchecked")
  public <T> FailureContext<T> getContext() {
    return (FailureContext<T>) context;
  }

}
