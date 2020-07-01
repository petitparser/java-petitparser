package org.petitparser.parser.actions;

import java.util.function.Function;

/**
 * Interface to define functions that need to access to the custom context
 *
 * @param <T> Parameter type
 * @param <R> Result type
 * @param <U> Custom context type
 */

@FunctionalInterface
public interface Action<T, R, U> extends Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @param u custom context
     * @return the function result
     */
    R apply(T t, U u);

    /**
     * Applies this function without context
     * @param t the function argument
     * @return the function result
     */
    @Override
    default R apply(T t) {
        return apply(t, null);
    }
}
