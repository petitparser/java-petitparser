package org.petitparser.parser.primitive;

import java.util.Arrays;

/**
 * Character predicate.
 */
@FunctionalInterface
public interface CharacterPredicate {

  /**
   * Returns a character predicate that matches any character.
   */
  static CharacterPredicate any() {
    return value -> true;
  }

  /**
   * Returns a character predicate that matches any of the characters in {@code string}.
   */
  static CharacterPredicate anyOf(String string) {
    char[] characters = string.toCharArray();
    Arrays.sort(characters);
    return value -> Arrays.binarySearch(characters, value) >= 0;
  }

  /**
   * Returns a character predicate that matches no character.
   */
  static CharacterPredicate none() {
    return value -> false;
  }

  /**
   * Returns a character predicate that matches none of the characters in {@code string}.
   */
  static CharacterPredicate noneOf(String string) {
    char[] characters = string.toCharArray();
    Arrays.sort(characters);
    return value -> Arrays.binarySearch(characters, value) < 0;
  }

  /**
   * Returns a character predicate that matches the given {@code character}.
   */
  static CharacterPredicate of(char character) {
    return value -> value == character;
  }

  /**
   * Returns a character predicate that matches any character between {@code start} and {@code stop}.
   */
  static CharacterPredicate range(char start, char stop) {
    return value -> start <= value && value <= stop;
  }

  /**
   * Tests if the character predicate is satisfied.
   */
  boolean test(char value);

  /**
   * Negates this character predicate.
   */
  default CharacterPredicate not() {
    return new NotCharacterPredicate(this);
  }

  /**
   * The negated character predicate.
   */
  class NotCharacterPredicate implements CharacterPredicate {

    private final CharacterPredicate predicate;

    public NotCharacterPredicate(CharacterPredicate predicate) {
      this.predicate = predicate;
    }

    @Override
    public boolean test(char value) {
      return !predicate.test(value);
    }

    @Override
    public CharacterPredicate not() {
      return predicate;
    }
  }

  /**
   * Matches either this character predicate or any of the other {@code predicates}.
   */
  default CharacterPredicate or(CharacterPredicate... others) {
    CharacterPredicate[] predicates = new CharacterPredicate[1 + others.length];
    predicates[0] = this;
    System.arraycopy(others, 0, predicates, 1, others.length);
    return new AltCharacterPredicate(predicates);
  }

  /**
   * The alternative character predicate.
   */
  class AltCharacterPredicate implements CharacterPredicate {

    private final CharacterPredicate[] predicates;

    public AltCharacterPredicate(CharacterPredicate... predicates) {
      this.predicates = predicates;
    }

    @Override
    public boolean test(char value) {
      for (CharacterPredicate predicate : predicates) {
        if (predicate.test(value)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public CharacterPredicate or(CharacterPredicate... others) {
      CharacterPredicate[] array = Arrays.copyOf(predicates, predicates.length + others.length);
      System.arraycopy(others, 0, array, predicates.length, others.length);
      return new AltCharacterPredicate(array);
    }
  }

}
