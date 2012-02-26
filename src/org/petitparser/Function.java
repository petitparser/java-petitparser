package org.petitparser;

public interface Function<T, R> {

  R apply(T argument);

}
