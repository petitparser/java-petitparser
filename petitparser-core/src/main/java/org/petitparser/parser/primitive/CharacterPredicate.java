package org.petitparser.parser.primitive;

import org.petitparser.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    return optimizedString(string);
  }

  /**
   * Returns a character predicate that matches any of the characters in {@code
   * string} with unicode option.
   */
  static CharacterPredicate anyOf(String string, boolean unicode) {
    return optimizedString(string, false, unicode);
  }

  /**
   * Returns a character predicate that matches any of the characters in {@code
   * string} with case-insensitivity and unicode options.
   */
  static CharacterPredicate anyOf(String string, boolean ignoreCase, boolean unicode) {
    return optimizedString(string, ignoreCase, unicode);
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
   * Returns a character predicate that matches none of the characters in {@code
   * string} with unicode option.
   */
  static CharacterPredicate noneOf(String string, boolean unicode) {
    return anyOf(string, unicode).not();
  }

  /**
   * Returns a character predicate that matches none of the characters in {@code
   * string} with case-insensitivity and unicode options.
   */
  static CharacterPredicate noneOf(String string, boolean ignoreCase, boolean unicode) {
    return anyOf(string, ignoreCase, unicode).not();
  }

  /**
   * Returns a character predicate that matches the given {@code character}.
   */
  static CharacterPredicate of(char character) {
    return new SingleCharPredicate(character);
  }

  /**
   * Returns a character predicate that matches the given code point.
   */
  static CharacterPredicate of(int codePoint) {
    return new SingleCharPredicate(codePoint);
  }

  /**
   * Returns a character predicate that matches any character between {@code
   * start} and {@code stop}.
   */
  static CharacterPredicate range(char start, char stop) {
    return new RangeCharPredicate(start, stop);
  }

  /**
   * Returns a character predicate that matches any code point between {@code
   * start} and {@code stop}.
   */
  static CharacterPredicate range(int start, int stop) {
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
   * Returns a character predicate that matches code point ranges between {@code
   * starts} and {@code stops}.
   */
  static CharacterPredicate ranges(int[] starts, int[] stops) {
    return new RangesCharPredicate(starts, stops);
  }

  /**
   * Returns a character predicate that matches the provided pattern.
   */
  static CharacterPredicate pattern(String pattern) {
    return PatternParser.PATTERN.parse(pattern).get();
  }

  /**
   * Returns a character predicate that matches the provided pattern with unicode option.
   */
  static CharacterPredicate pattern(String pattern, boolean unicode) {
    return CharacterParser.pattern(pattern, unicode).getMatcher();
  }

  /**
   * Returns a character predicate that matches the provided pattern with case-insensitivity and unicode options.
   */
  static CharacterPredicate pattern(String pattern, boolean ignoreCase, boolean unicode) {
    return CharacterParser.pattern(pattern, null, ignoreCase, unicode).getMatcher();
  }

  /**
   * Creates an optimized character predicate from a string.
   */
  static CharacterPredicate optimizedString(String string) {
    return optimizedString(string, false, false);
  }

  /**
   * Creates an optimized character predicate from a string with case-insensitivity option.
   */
  static CharacterPredicate optimizedString(String string, boolean ignoreCase) {
    return optimizedString(string, ignoreCase, false);
  }

  /**
   * Creates an optimized character predicate from a string with case-insensitivity and unicode options.
   */
  static CharacterPredicate optimizedString(
      String string, boolean ignoreCase, boolean unicode) {
    if (ignoreCase) {
      string = string.toLowerCase(Locale.ROOT) + string.toUpperCase(Locale.ROOT);
    }
    List<RangeCharPredicate> ranges = new ArrayList<>();
    if (unicode) {
      string.codePoints().forEach(cp -> ranges.add(new RangeCharPredicate(cp, cp)));
    } else {
      for (int i = 0; i < string.length(); i++) {
        char c = string.charAt(i);
        ranges.add(new RangeCharPredicate(c, c));
      }
    }
    return optimizedRanges(ranges, unicode);
  }

  /**
   * Creates an optimized character predicate from a list of range predicates.
   */
  static CharacterPredicate optimizedRanges(List<RangeCharPredicate> ranges) {
    return optimizedRanges(ranges, false);
  }

  /**
   * Creates an optimized character predicate from a list of range predicates with unicode option.
   */
  static CharacterPredicate optimizedRanges(
      List<RangeCharPredicate> ranges, boolean unicode) {
    // 1. Sort the ranges
    List<RangeCharPredicate> sortedRanges = new ArrayList<>(ranges);
    sortedRanges.sort((first, second) -> {
      if (first.getStart() != second.getStart()) {
        return Integer.compare(first.getStart(), second.getStart());
      }
      return Integer.compare(first.getStop(), second.getStop());
    });

    // 2. Merge adjacent or overlapping ranges
    List<RangeCharPredicate> mergedRanges = new ArrayList<>();
    for (RangeCharPredicate thisRange : sortedRanges) {
      if (mergedRanges.isEmpty()) {
        mergedRanges.add(thisRange);
      } else {
        RangeCharPredicate lastRange = mergedRanges.get(mergedRanges.size() - 1);
        if ((long) lastRange.getStop() + 1 >= thisRange.getStart()) {
          RangeCharPredicate merged = new RangeCharPredicate(
              lastRange.getStart(),
              Math.max(lastRange.getStop(), thisRange.getStop()));
          mergedRanges.set(mergedRanges.size() - 1, merged);
        } else {
          mergedRanges.add(thisRange);
        }
      }
    }

    // 3. Build the best resulting predicate
    if (mergedRanges.isEmpty()) {
      return ConstantCharPredicate.none();
    } else if (mergedRanges.size() == 1) {
      RangeCharPredicate range = mergedRanges.get(0);
      if (range.getStart() <= 0 &&
          range.getStop() >= (unicode ? 0x10ffff : 0xffff)) {
        return ConstantCharPredicate.any();
      } else if (range.getStart() == range.getStop()) {
        return new SingleCharPredicate(range.getStart());
      } else {
        return range;
      }
    } else {
      int lookupBytes = (mergedRanges.get(mergedRanges.size() - 1).getStop()
          - mergedRanges.get(0).getStart() + 32) >> 3;
      int rangesBytes = mergedRanges.size() * 8;
      if (lookupBytes > 1024 && rangesBytes < (lookupBytes >> 3)) {
        return RangesCharPredicate.fromRanges(mergedRanges);
      }
      return LookupCharPredicate.fromRanges(mergedRanges);
    }
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
   *
   * @deprecated Use {@link NotCharPredicate} or {@link CharacterPredicate#not()} instead.
   */
  @Deprecated(since = "2.5.0")
  class NotCharacterPredicate extends NotCharPredicate {
    public NotCharacterPredicate(CharacterPredicate predicate) {
      super(predicate);
    }
  }
}
