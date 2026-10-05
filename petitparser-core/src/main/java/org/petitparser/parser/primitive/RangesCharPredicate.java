package org.petitparser.parser.primitive;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Character predicate matching multiple non-overlapping sorted ranges of characters.
 */
public class RangesCharPredicate implements CharacterPredicate {

  private final int[] starts;
  private final int[] stops;

  public RangesCharPredicate(char[] starts, char[] stops) {
    if (starts.length != stops.length) {
      throw new IllegalArgumentException("Invalid range sizes.");
    }
    this.starts = new int[starts.length];
    this.stops = new int[stops.length];
    for (int i = 0; i < starts.length; i++) {
      if (starts[i] > stops[i]) {
        throw new IllegalArgumentException(
            "Invalid range: " + starts[i] + "-" + stops[i]);
      }
      if (i + 1 < starts.length && starts[i + 1] <= stops[i]) {
        throw new IllegalArgumentException("Invalid sequence.");
      }
      this.starts[i] = starts[i];
      this.stops[i] = stops[i];
    }
  }

  public RangesCharPredicate(int[] starts, int[] stops) {
    if (starts.length != stops.length) {
      throw new IllegalArgumentException("Invalid range sizes.");
    }
    for (int i = 0; i < starts.length; i++) {
      if (starts[i] > stops[i]) {
        throw new IllegalArgumentException(
            "Invalid range: " + starts[i] + "-" + stops[i]);
      }
      if (i + 1 < starts.length && starts[i + 1] <= stops[i]) {
        throw new IllegalArgumentException("Invalid sequence.");
      }
    }
    this.starts = starts.clone();
    this.stops = stops.clone();
  }

  public static RangesCharPredicate fromRanges(List<RangeCharPredicate> ranges) {
    int[] starts = new int[ranges.size()];
    int[] stops = new int[ranges.size()];
    for (int i = 0; i < ranges.size(); i++) {
      starts[i] = ranges.get(i).getStart();
      stops[i] = ranges.get(i).getStop();
    }
    return new RangesCharPredicate(starts, stops);
  }

  public int[] getStarts() {
    return starts.clone();
  }

  public int[] getStops() {
    return stops.clone();
  }

  @Override
  public boolean test(char value) {
    return test((int) value);
  }

  @Override
  public boolean test(int value) {
    int index = Arrays.binarySearch(starts, value);
    return index >= 0 || index < -1 && value <= stops[-index - 2];
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof RangesCharPredicate &&
        Arrays.equals(starts, ((RangesCharPredicate) other).starts) &&
        Arrays.equals(stops, ((RangesCharPredicate) other).stops);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof CharacterPredicate &&
        isEqualTo((CharacterPredicate) other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Arrays.hashCode(starts), Arrays.hashCode(stops));
  }

  @Override
  public String toString() {
    return "RangesCharPredicate[" + starts.length + " ranges]";
  }
}
