package org.petitparser.parser.primitive;

/**
 * Character predicate matching a single character.
 */
public class SingleCharPredicate implements CharacterPredicate {

  private final int value;

  public SingleCharPredicate(char value) {
    this((int) value);
  }

  public SingleCharPredicate(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  @Override
  public boolean test(char value) {
    return value == this.value;
  }

  @Override
  public boolean test(int value) {
    return value == this.value;
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof SingleCharPredicate &&
        value == ((SingleCharPredicate) other).value;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof CharacterPredicate &&
        isEqualTo((CharacterPredicate) other);
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(value);
  }

  @Override
  public String toString() {
    return "SingleCharPredicate[" + value + "]";
  }
}
