package org.petitparser.parser.primitive;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Objects;

/**
 * Parser for an individual Unicode code point (including surrogate pairs)
 * satisfying a specified {@link CharacterPredicate}.
 */
public class UnicodeCharacterParser extends CharacterParser {

  public UnicodeCharacterParser(CharacterPredicate matcher, String message) {
    super(matcher, message);
  }

  @Override
  public Result parseOn(Context context) {
    String buffer = context.getBuffer();
    int position = context.getPosition();
    if (position < buffer.length()) {
      char ch = buffer.charAt(position);
      int nextPosition = position + 1;
      int codePoint = ch;
      if (Character.isHighSurrogate(ch) && nextPosition < buffer.length()) {
        char nextCh = buffer.charAt(nextPosition);
        if (Character.isLowSurrogate(nextCh)) {
          codePoint = Character.toCodePoint(ch, nextCh);
          nextPosition++;
        }
      }
      if (getMatcher().test(codePoint)) {
        return context.success(buffer.substring(position, nextPosition), nextPosition);
      }
    }
    return context.failure(getMessage());
  }

  @Override
  public int fastParseOn(String buffer, int position) {
    if (position < buffer.length()) {
      char ch = buffer.charAt(position);
      int nextPosition = position + 1;
      int codePoint = ch;
      if (Character.isHighSurrogate(ch) && nextPosition < buffer.length()) {
        char nextCh = buffer.charAt(nextPosition);
        if (Character.isLowSurrogate(nextCh)) {
          codePoint = Character.toCodePoint(ch, nextCh);
          nextPosition++;
        }
      }
      if (getMatcher().test(codePoint)) {
        return nextPosition;
      }
    }
    return -1;
  }

  @Override
  public UnicodeCharacterParser copy() {
    return new UnicodeCharacterParser(getMatcher(), getMessage());
  }

  @Override
  public UnicodeCharacterParser neg(String message) {
    return new UnicodeCharacterParser(getMatcher().not(), message);
  }

  @Override
  protected boolean hasEqualProperties(Parser other) {
    return super.hasEqualProperties(other) && other instanceof UnicodeCharacterParser;
  }
}
