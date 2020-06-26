package org.petitparser.parser.primitive;

import org.petitparser.parser.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    List<CharacterRange> ranges = string.chars()
        .mapToObj(value -> new CharacterRange((char) value, (char) value))
        .collect(Collectors.toList());
    return CharacterRange.toCharacterPredicate(ranges);
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
    List<CharacterRange> ranges = string.chars()
        .mapToObj(value -> new CharacterRange((char) value, (char) value))
        .collect(Collectors.toList());
    return CharacterRange.toCharacterPredicate(ranges).not();
  }

  /**
   * Returns a character predicate that matches the given {@code character}.
   */
  static CharacterPredicate of(char character) {
    return value -> value == character;
  }

  /**
   * Returns a character predicate that matches any character between {@code start} and {@code
   * stop}.
   */
  static CharacterPredicate range(char start, char stop) {
    return value -> start <= value && value <= stop;
  }

  /**
   * Returns a character predicate that matches character ranges between {@code starts} and {@code
   * stops}.
   */
  static CharacterPredicate ranges(char[] starts, char[] stops) {
    if (starts.length != stops.length) {
      throw new IllegalArgumentException("Invalid range sizes.");
    }
    for (int i = 0; i < starts.length; i++) {
      if (starts[i] > stops[i]) {
        throw new IllegalArgumentException("Invalid range: " + starts[i] + "-" + stops[i]);
      }
      if (i + 1 < starts.length && starts[i + 1] <= stops[i]) {
        throw new IllegalArgumentException("Invalid sequence.");
      }
    }
    return value -> {
      int index = Arrays.binarySearch(starts, value);
      return index >= 0 || index < -1 && value <= stops[-index - 2];
    };
  }

  /**
   * Returns a character predicate that matches the provided pattern.
   */
  static CharacterPredicate pattern(String pattern) {
    return (CharacterPredicate) PatternParser.PATTERN.parse(pattern).get();
  }

  class PatternParser {
    static final Parser PATTERN_SIMPLE = CharacterParser.any()
        .map((Character value) -> new CharacterRange(value, value));
    static final Parser PATTERN_RANGE = CharacterParser.any()
        .seq(CharacterParser.of('-'))
        .seq(CharacterParser.any())
        .map((List<Character> values) -> new CharacterRange(values.get(0), values.get(2)));
    static final Parser PATTERN_POSITIVE = PATTERN_RANGE
        .or(PATTERN_SIMPLE).star()
        .map(CharacterRange::toCharacterPredicate);
    static final Parser PATTERN = CharacterParser.of('^').optional()
        .seq(PATTERN_POSITIVE)
        .map((List<CharacterPredicate> predicate) -> {
          return predicate.get(0) == null ? predicate.get(1) : predicate.get(1).not();
        }).end();
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
}
