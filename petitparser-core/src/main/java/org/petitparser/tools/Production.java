package org.petitparser.tools;

import java.util.Objects;

/**
 * A type-safe production key for {@link GrammarDefinition}.
 *
 * @param <T> the parse result type produced by this production.
 */
public final class Production<T> {

  private final String name;

  private Production(String name) {
    this.name = Objects.requireNonNull(name, "Undefined name");
  }

  /**
   * Creates a typed production key with the given {@code name}.
   *
   * @param name the production name.
   * @param <T> the parse result type.
   * @return a production key.
   */
  public static <T> Production<T> of(String name) {
    return new Production<>(name);
  }

  /**
   * Returns the name of this production.
   *
   * @return the production name.
   */
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Production<?> that = (Production<?>) o;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return "Production[" + name + "]";
  }
}
