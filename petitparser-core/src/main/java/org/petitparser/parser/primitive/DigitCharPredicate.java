package org.petitparser.parser.primitive;

/**
 * Character predicate matching digit characters.
 */
public class DigitCharPredicate implements CharacterPredicate {

  public static final DigitCharPredicate INSTANCE = new DigitCharPredicate();

  @Override
  public boolean test(char value) {
    return Character.isDigit(value);
  }

  @Override
  public boolean test(int value) {
    return Character.isDigit(value);
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof DigitCharPredicate;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof DigitCharPredicate;
  }

  @Override
  public int hashCode() {
    return DigitCharPredicate.class.hashCode();
  }

  @Override
  public String toString() {
    return "DigitCharPredicate";
  }
}
