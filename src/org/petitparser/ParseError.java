package org.petitparser;

import org.petitparser.context.FailureContext;

public class ParseError extends RuntimeException {

  private static final long serialVersionUID = 1865114289785709043L;

  private final FailureContext context;

  public ParseError(FailureContext context) {
    super(context.getMessage());
    this.context = context;
  }

  public FailureContext getContext() {
    return context;
  }

}
