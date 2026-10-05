package org.petitparser.parser.primitive;

/**
 * Character predicate matching uppercase characters.
 */
public class UppercaseCharPredicate implements CharacterPredicate {

  public static final UppercaseCharPredicate INSTANCE = new UppercaseCharPredicate();

  @Override
  public boolean test(char value) {
    return Character.isUpperCase(value);
  }

  @Override
  public boolean test(int value) {
    return Character.isUpperCase(value);
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof UppercaseCharPredicate;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof UppercaseCharPredicate;
  }

  @Override
  public int hashCode() {
    return UppercaseCharPredicate.class.hashCode();
  }

  @Override
  public String toString() {
    return "UppercaseCharPredicate";
  }
}
