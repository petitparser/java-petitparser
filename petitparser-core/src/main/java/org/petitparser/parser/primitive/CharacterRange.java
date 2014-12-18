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
        return first.start < second.start ? -1
            : first.start > second.start ? +1
                : first.stop < second.stop ? -1
                    : first.stop > second.stop ? +1
                        : 0;
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

    // 3. build the corresponding predicates
    List<CharacterPredicate> predicates = new ArrayList<>();
    for (CharacterRange range : mergedRanges) {
      if (range.stop - range.start > 1) {
        predicates.add(CharacterPredicate.range(range.start, range.stop));
      } else {
        for (char value = range.start; value <= range.stop; value++) {
          predicates.add(CharacterPredicate.of(value));
        }
      }
    }

    // 4. when necessary build a composite predicate
    return predicates.size() == 1
        ? predicates.get(0)
        : new CharacterPredicate.AltCharacterPredicate(
            predicates.toArray(new CharacterPredicate[predicates.size()]));
  }

  private final char start;
  private final char stop;

  CharacterRange(char start) {
    this(start, start);
  }

  CharacterRange(char start, char stop) {
    this.start = start;
    this.stop = stop;
  }
}
