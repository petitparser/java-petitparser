package org.petitparser.parser.actions;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * A parser that evaluates a predicate on the successful result of its delegate.
 *
 * @param <T> The type of the result to filter.
 */
public class WhereParser<T> extends DelegateParser {

  protected final Predicate<T> predicate;
  protected final String message;
  protected final BiFunction<Context, Result, Result> failureFactory;

  /**
   * Constructs a where parser with a default failure message.
   *
   * @param delegate the delegate parser.
   * @param predicate the predicate to evaluate on parse success.
   */
  public WhereParser(Parser delegate, Predicate<T> predicate) {
    this(delegate, predicate, null, null);
  }

  /**
   * Constructs a where parser with a custom failure message.
   *
   * @param delegate the delegate parser.
   * @param predicate the predicate to evaluate on parse success.
   * @param message the failure message if the predicate returns {@code false}.
   */
  public WhereParser(Parser delegate, Predicate<T> predicate, String message) {
    this(delegate, predicate, message, null);
  }

  /**
   * Constructs a where parser with a custom failure factory.
   *
   * @param delegate the delegate parser.
   * @param predicate the predicate to evaluate on parse success.
   * @param failureFactory the factory function to create a failure result.
   */
  public WhereParser(
      Parser delegate,
      Predicate<T> predicate,
      BiFunction<Context, Result, Result> failureFactory) {
    this(delegate, predicate, null,
        Objects.requireNonNull(failureFactory, "Undefined failure factory"));
  }

  protected WhereParser(
      Parser delegate,
      Predicate<T> predicate,
      String message,
      BiFunction<Context, Result, Result> failureFactory) {
    super(delegate);
    this.predicate = Objects.requireNonNull(predicate, "Undefined predicate");
    this.message = message;
    this.failureFactory = failureFactory;
  }

  /**
   * Returns the predicate used to filter parse results.
   */
  public Predicate<T> getPredicate() {
    return predicate;
  }

  /**
   * Returns the failure message if configured, or {@code null}.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the failure factory if configured, or {@code null}.
   */
  public BiFunction<Context, Result, Result> getFailureFactory() {
    return failureFactory;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Result parseOn(Context context) {
    Result result = delegate.parseOn(context);
    if (result.isSuccess() && !predicate.test((T) result.get())) {
      if (failureFactory != null) {
        return failureFactory.apply(context, result);
      }
      return context.failure(message != null ? message : "unexpected \"" + result.get() + "\"");
    }
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public int fastParseOn(String buffer, int position) {
    Result result = delegate.parseOn(new Context(buffer, position));
    return result.isSuccess() && predicate.test((T) result.get())
        ? result.getPosition()
        : -1;
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) &&
        Objects.equals(predicate, ((WhereParser<T>) other).predicate) &&
        Objects.equals(message, ((WhereParser<T>) other).message) &&
        Objects.equals(failureFactory, ((WhereParser<T>) other).failureFactory);
  }

  @Override
  public WhereParser<T> copy() {
    return new WhereParser<>(delegate, predicate, message, failureFactory);
  }
}
