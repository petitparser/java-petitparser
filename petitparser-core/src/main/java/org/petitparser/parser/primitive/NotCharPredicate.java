package org.petitparser.parser.primitive;

import java.util.Objects;

/**
 * Character predicate negating another predicate.
 */
public class NotCharPredicate implements CharacterPredicate {

  private final CharacterPredicate predicate;

  public NotCharPredicate(CharacterPredicate predicate) {
    this.predicate = Objects.requireNonNull(predicate, "Undefined predicate");
  }

  public CharacterPredicate getPredicate() {
    return predicate;
  }

  @Override
  public boolean test(char value) {
    return !predicate.test(value);
  }

  @Override
  public boolean test(int value) {
    return !predicate.test(value);
  }

  @Override
  public CharacterPredicate not() {
    return predicate;
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof NotCharPredicate that &&
        predicate.isEqualTo(that.predicate);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof CharacterPredicate that &&
        isEqualTo(that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(predicate);
  }

  @Override
  public String toString() {
    return "NotCharPredicate[" + predicate + "]";
  }
}
