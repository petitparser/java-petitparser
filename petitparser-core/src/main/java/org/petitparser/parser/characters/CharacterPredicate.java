package org.petitparser.parser.characters;

import java.util.Arrays;

/**
 * Character predicate.
 */
@FunctionalInterface
public interface CharacterPredicate {

  /**
   * Returns a character matcher that matches any character.
   */
  static CharacterPredicate any() {
    return value -> true;
  }

  /**
   * Returns a character matcher that matches any of the characters in {@code string}.
   */
  static CharacterPredicate anyOf(String string) {
    char[] characters = string.toCharArray();
    Arrays.sort(characters);
    return value -> Arrays.binarySearch(characters, value) >= 0;
  }

  /**
   * Returns a character matcher that matches no character.
   */
  static CharacterPredicate none() {
    return value -> false;
  }

  /**
   * Returns a character matcher that matches none of the characters in {@code string}.
   */
  static CharacterPredicate noneOf(String string) {
    char[] characters = string.toCharArray();
    Arrays.sort(characters);
    return value -> Arrays.binarySearch(characters, value) < 0;
  }

  /**
   * Returns a character matcher that matches the given {@code character}.
   */
  static CharacterPredicate of(char character) {
    return value -> value == character;
  }

  /**
   * Returns a character matcher that matches any character between {@code start} and {@code stop}.
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
    return value -> !test(value);
  }

  /**
   * Matches either this character predicate or {@code other}.
   */
  default CharacterPredicate or(CharacterPredicate other) {
    return value -> test(value) || other.test(value);
  }
}
