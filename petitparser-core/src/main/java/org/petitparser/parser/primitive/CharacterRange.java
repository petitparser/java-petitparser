package org.petitparser.parser.primitive;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal class to build an optimized {@link CharacterPredicate} from single
 * characters or ranges of characters.
 */
class CharacterRange {

  static CharacterPredicate toCharacterPredicate(List<CharacterRange> ranges) {
    List<RangeCharPredicate> list = new ArrayList<>(ranges.size());
    for (CharacterRange range : ranges) {
      list.add(new RangeCharPredicate(range.start, range.stop));
    }
    return CharacterPredicate.optimizedRanges(list, false);
  }

  final char start;
  final char stop;

  CharacterRange(char start, char stop) {
    this.start = start;
    this.stop = stop;
  }
}
