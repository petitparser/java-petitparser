package org.petitparser.parser.primitive;

/**
 * Character predicate matching lowercase characters.
 */
public class LowercaseCharPredicate implements CharacterPredicate {

  public static final LowercaseCharPredicate INSTANCE = new LowercaseCharPredicate();

  @Override
  public boolean test(char value) {
    return Character.isLowerCase(value);
  }

  @Override
  public boolean test(int value) {
    return Character.isLowerCase(value);
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof LowercaseCharPredicate;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof LowercaseCharPredicate;
  }

  @Override
  public int hashCode() {
    return LowercaseCharPredicate.class.hashCode();
  }

  @Override
  public String toString() {
    return "LowercaseCharPredicate";
  }
}
