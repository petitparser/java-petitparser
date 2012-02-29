package org.petitparser;

/**
 * Abstract parse context with input and output state.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public abstract class Context {

  int position;
  String message;
  Object result;

}
