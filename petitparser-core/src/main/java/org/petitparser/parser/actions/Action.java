package org.petitparser.parser.actions;

import java.util.function.Function;

@FunctionalInterface
public interface Action<T, R, U> extends Function<T, R>
{
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t, U u);

    @Override
    default R apply(T t)
    {
        return apply(t, null);
    }
}
