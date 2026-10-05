package org.petitparser.parser.primitive;

/**
 * Character predicate matching letter characters.
 */
public class LetterCharPredicate implements CharacterPredicate {

  public static final LetterCharPredicate INSTANCE = new LetterCharPredicate();

  @Override
  public boolean test(char value) {
    return Character.isLetter(value);
  }

  @Override
  public boolean test(int value) {
    return Character.isLetter(value);
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof LetterCharPredicate;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof LetterCharPredicate;
  }

  @Override
  public int hashCode() {
    return LetterCharPredicate.class.hashCode();
  }

  @Override
  public String toString() {
    return "LetterCharPredicate";
  }
}
