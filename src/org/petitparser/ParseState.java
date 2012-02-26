package org.petitparser;

/**
 * Functionality to snapshot and restore state.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public interface ParseState<T> {

  /**
   * Snapshots the state of the receiver.
   */
  T snapshot();

  /**
   * Restores state that has been previously snapshot.
   */
  void restore(T state);

}
