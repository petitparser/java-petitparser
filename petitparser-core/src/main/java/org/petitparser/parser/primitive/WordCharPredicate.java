package org.petitparser.parser.primitive;

/**
 * Character predicate matching word characters (letters or digits).
 */
public class WordCharPredicate implements CharacterPredicate {

  public static final WordCharPredicate INSTANCE = new WordCharPredicate();

  @Override
  public boolean test(char value) {
    return Character.isLetterOrDigit(value);
  }

  @Override
  public boolean test(int value) {
    return Character.isLetterOrDigit(value);
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof WordCharPredicate;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof WordCharPredicate;
  }

  @Override
  public int hashCode() {
    return WordCharPredicate.class.hashCode();
  }

  @Override
  public String toString() {
    return "WordCharPredicate";
  }
}
