package org.petitparser.parser.primitive;

import org.petitparser.parser.Parser;

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
    return ConstantCharPredicate.any();
  }

  /**
   * Returns a character predicate that matches any of the characters in {@code
   * string}.
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
    return ConstantCharPredicate.none();
  }

  /**
   * Returns a character predicate that matches none of the characters in {@code
   * string}.
   */
  static CharacterPredicate noneOf(String string) {
    return anyOf(string).not();
  }

  /**
   * Returns a character predicate that matches the given {@code character}.
   */
  static CharacterPredicate of(char character) {
    return new SingleCharPredicate(character);
  }

  /**
   * Returns a character predicate that matches any character between {@code
   * start} and {@code stop}.
   */
  static CharacterPredicate range(char start, char stop) {
    return new RangeCharPredicate(start, stop);
  }

  /**
   * Returns a character predicate that matches character ranges between {@code
   * starts} and {@code stops}.
   */
  static CharacterPredicate ranges(char[] starts, char[] stops) {
    return new RangesCharPredicate(starts, stops);
  }

  /**
   * Returns a character predicate that matches the provided pattern.
   */
  static CharacterPredicate pattern(String pattern) {
    return PatternParser.PATTERN.parse(pattern).get();
  }

  class PatternParser {
    static final Parser PATTERN_SIMPLE = CharacterParser.any()
        .map((Character value) -> new CharacterRange(value, value));
    static final Parser PATTERN_RANGE =
        CharacterParser.any().seq(CharacterParser.of('-'))
            .seq(CharacterParser.any()).map(
                (List<Character> values) -> new CharacterRange(values.get(0),
                    values.get(2)));
    static final Parser PATTERN_POSITIVE =
        PATTERN_RANGE.or(PATTERN_SIMPLE).star()
            .map(CharacterRange::toCharacterPredicate);
    static final Parser PATTERN =
        CharacterParser.of('^').optional().seq(PATTERN_POSITIVE)
            .map((List<CharacterPredicate> predicate) -> {
              return predicate.get(0) == null ? predicate.get(1) :
                  predicate.get(1).not();
            }).end();
  }

  /**
   * Tests if the character predicate is satisfied.
   */
  boolean test(char value);

  /**
   * Tests if the character predicate is satisfied for a Unicode code point.
   */
  default boolean test(int value) {
    return value >= 0 && value <= Character.MAX_VALUE && test((char) value);
  }

  /**
   * Negates this character predicate.
   */
  default CharacterPredicate not() {
    return new NotCharPredicate(this);
  }

  /**
   * Tests for structural equality of two character predicates.
   */
  default boolean isEqualTo(CharacterPredicate other) {
    return equals(other);
  }

  /**
   * Backward compatibility alias for {@link NotCharPredicate}.
   */
  class NotCharacterPredicate extends NotCharPredicate {
    public NotCharacterPredicate(CharacterPredicate predicate) {
      super(predicate);
    }
  }
}
