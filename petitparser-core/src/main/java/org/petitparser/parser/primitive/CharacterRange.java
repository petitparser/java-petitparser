package org.petitparser.parser.primitive;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Internal class to build an optimized {@link CharacterPredicate} from single characters or ranges
 * of characters.
 */
class CharacterRange {

  static final Comparator<CharacterRange> CHARACTER_RANGE_COMPARATOR =
      Comparator
          .comparing((CharacterRange range) -> range.start)
          .thenComparing((CharacterRange range) -> range.stop);


  private final char start;
  private final char stop;

  CharacterRange(char start, char stop) {
    this.start = start;
    this.stop = stop;
  }

  static CharacterPredicate toCharacterPredicate(List<CharacterRange> ranges) {

    // 1. sort the ranges
    List<CharacterRange> sortedRanges = new ArrayList<>(ranges);
    sortedRanges.sort(CHARACTER_RANGE_COMPARATOR);

    // 2. merge adjacent or overlapping ranges
    List<CharacterRange> mergedRanges = new ArrayList<>();
    for (CharacterRange thisRange : sortedRanges) {
      if (mergedRanges.isEmpty()) {
        mergedRanges.add(thisRange);
      } else {
        CharacterRange lastRange = mergedRanges.get(mergedRanges.size() - 1);
        if (lastRange.stop + 1 >= thisRange.start) {
          CharacterRange characterRange = new CharacterRange(lastRange.start, thisRange.stop);
          mergedRanges.set(mergedRanges.size() - 1, characterRange);
        } else {
          mergedRanges.add(thisRange);
        }
      }
    }

    // 3. build the best resulting predicates
    if (mergedRanges.isEmpty()) {
      return CharacterPredicate.none();
    } else if (mergedRanges.size() == 1) {
      CharacterRange characterRange = mergedRanges.get(0);
      return characterRange.start == characterRange.stop
          ? CharacterPredicate.of(characterRange.start)
          : CharacterPredicate.range(characterRange.start, characterRange.stop);
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

}
