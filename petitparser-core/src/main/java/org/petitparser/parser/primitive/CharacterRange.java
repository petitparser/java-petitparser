package org.petitparser.parser.primitive;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Internal class to build a {@link CharacterPredicate} from single characters or ranges of
 * characters.
 */
class CharacterRange {

  static CharacterPredicate toCharacterPredicate(List<CharacterRange> ranges) {

    // 1. sort the ranges
    List<CharacterRange> sortedRanges = new ArrayList<>(ranges);
    sortedRanges.sort(new Comparator<CharacterRange>() {
      @Override
      public int compare(CharacterRange first, CharacterRange second) {
        return Character.compare(first.start, second.start);
      }
    });

    // 2. merge adjacent or overlapping ranges
    List<CharacterRange> mergedRanges = new ArrayList<>();
    for (CharacterRange currentRange : sortedRanges) {
      if (mergedRanges.isEmpty()) {
        mergedRanges.add(currentRange);
      } else {
        CharacterRange lastRange = mergedRanges.get(mergedRanges.size() - 1);
        if (lastRange.stop + 1 >= currentRange.start) {
          mergedRanges.set(mergedRanges.size() - 1, new CharacterRange(
              lastRange.start < currentRange.start ? lastRange.start : currentRange.start,
              lastRange.stop > currentRange.stop ? lastRange.stop : currentRange.stop));
        } else {
          mergedRanges.add(currentRange);
        }
      }
    }

    // 3. build the best resulting predicates
    if (mergedRanges.isEmpty()) {
      return CharacterPredicate.none();
    } else if (mergedRanges.size() == 1) {
      return mergedRanges.get(0).start == mergedRanges.get(0).stop
          ? CharacterPredicate.of(mergedRanges.get(0).start)
          : CharacterPredicate.range(mergedRanges.get(0).start, mergedRanges.get(0).stop);
    } else {
      char[] starts = new char[mergedRanges.size()];
      char[] stops = new char[mergedRanges.size()];
      for (int i = 0; i < mergedRanges.size(); i++) {
        starts[i] = mergedRanges.get(i).start;
        stops[i] = mergedRanges.get(i).stop;
      }
      return CharacterPredicate.ranges(starts, stops);
    }

  }

  private final char start;
  private final char stop;

  CharacterRange(char start, char stop) {
    this.start = start;
    this.stop = stop;
  }
}
