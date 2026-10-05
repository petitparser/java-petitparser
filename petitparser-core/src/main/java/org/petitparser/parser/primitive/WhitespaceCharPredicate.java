package org.petitparser.parser.primitive;

/**
 * Character predicate matching whitespace characters.
 */
public class WhitespaceCharPredicate implements CharacterPredicate {

  public static final WhitespaceCharPredicate INSTANCE = new WhitespaceCharPredicate();

  @Override
  public boolean test(char value) {
    return Character.isWhitespace(value);
  }

  @Override
  public boolean test(int value) {
    return Character.isWhitespace(value);
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof WhitespaceCharPredicate;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof WhitespaceCharPredicate;
  }

  @Override
  public int hashCode() {
    return WhitespaceCharPredicate.class.hashCode();
  }

  @Override
  public String toString() {
    return "WhitespaceCharPredicate";
  }
}
