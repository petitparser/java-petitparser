package org.petitparser.parser.primitive;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Character predicate performing O(1) bitset lookup.
 */
public class LookupCharPredicate implements CharacterPredicate {

  private final int start;
  private final int stop;
  private final int[] bits;

  public LookupCharPredicate(int start, int stop, int[] bits) {
    if (start > stop) {
      throw new IllegalArgumentException("Invalid range: " + start + "-" + stop);
    }
    this.start = start;
    this.stop = stop;
    this.bits = bits.clone();
  }

  public LookupCharPredicate(boolean[] table) {
    if (table.length == 0) {
      throw new IllegalArgumentException("Table cannot be empty");
    }
    this.start = 0;
    this.stop = table.length - 1;
    this.bits = new int[(table.length + 31) >> 5];
    for (int i = 0; i < table.length; i++) {
      if (table[i]) {
        this.bits[i >> 5] |= (1 << (i & 31));
      }
    }
  }

  public static LookupCharPredicate fromRanges(List<RangeCharPredicate> ranges) {
    if (ranges.isEmpty()) {
      throw new IllegalArgumentException("Empty ranges");
    }
    int start = ranges.get(0).getStart();
    int stop = ranges.get(ranges.size() - 1).getStop();
    int size = ((stop - start) + 32) >> 5;
    int[] bits = new int[size];
    for (RangeCharPredicate range : ranges) {
      for (int i = range.getStart() - start; i <= range.getStop() - start; i++) {
        bits[i >> 5] |= (1 << (i & 31));
      }
    }
    return new LookupCharPredicate(start, stop, bits);
  }

  public int getStart() {
    return start;
  }

  public int getStop() {
    return stop;
  }

  public int[] getBits() {
    return bits.clone();
  }

  @Override
  public boolean test(char value) {
    return test((int) value);
  }

  @Override
  public boolean test(int value) {
    return start <= value && value <= stop &&
        (bits[(value - start) >> 5] & (1 << ((value - start) & 31))) != 0;
  }

  @Override
  public boolean isEqualTo(CharacterPredicate other) {
    return other instanceof LookupCharPredicate that &&
        start == that.start &&
        stop == that.stop &&
        Arrays.equals(bits, that.bits);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof CharacterPredicate that &&
        isEqualTo(that);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, stop, Arrays.hashCode(bits));
  }

  @Override
  public String toString() {
    return "LookupCharPredicate[" + start + ".." + stop + "]";
  }
}
