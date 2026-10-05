package org.petitparser.parser.primitive;

import java.util.Objects;

/**
 * Character predicate matching a range of characters.
 */
public class RangeCharPredicate implements CharacterPredicate {

  public static CharacterPredicate optimizedRanges(java.util.List<RangeCharPredicate> ranges) {
    return CharacterPredicate.optimizedRanges(ranges);
  }

  public static CharacterPredicate optimizedRanges(
      java.util.List<RangeCharPredicate> ranges, boolean unicode) {
    return CharacterPredicate.optimizedRanges(ranges, unicode);
  }

  private final int start;
  private final int stop;

  public RangeCharPredicate(char start, char stop) {
    this((int) start, (int) stop);
  }

  public RangeCharPredicate(int start, int stop) {
    if (start > stop) {
      throw new IllegalArgumentException("Invalid range: " + start + "-" + stop);
    }
    this.start = start;
    this.stop = stop;
  }

  public int getStart() {
    return start;
  }

  public int getStop() {
    return stop;
  }

  @Override
  public boolean test(char value) {
    return start <= value && value <= stop;
  }

  @Override
  public boolean test(int value) {
    return start <= value && value <= stop;
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof RangeCharPredicate &&
        start == ((RangeCharPredicate) other).start &&
        stop == ((RangeCharPredicate) other).stop;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof CharacterPredicate &&
        isEqualTo((CharacterPredicate) other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, stop);
  }

  @Override
  public String toString() {
    return "RangeCharPredicate[" + start + ".." + stop + "]";
  }
}
