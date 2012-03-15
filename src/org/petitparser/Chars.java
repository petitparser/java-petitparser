package org.petitparser;

import java.util.List;

import org.petitparser.context.Context;
import org.petitparser.parser.CharParser;
import org.petitparser.parser.CharParser.CharPredicate;
import org.petitparser.parser.Parser;

import com.google.common.base.Function;

/**
 * Factory for character parsers.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class Chars {

  /**
   * Returns a parser that parses any character.
   */
  public static Parser any() {
    return any("character expected");
  }

  public static Parser any(String message) {
    return new CharParser(anyPredicate(), message);
  }

  private static CharPredicate anyPredicate() {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return true;
      }
    };
  }

  /**
   * Returns a parser that parses a specific {@code character}.
   */
  public static Parser character(char character) {
    return character(character, character + " expected");
  }

  public static Parser character(char character, String message) {
    return new CharParser(characterPredicate(character), message);
  }

  private static CharPredicate characterPredicate(final char character) {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return character == input;
      }
    };
  }

  /**
   * Returns a parser that parses a single digit.
   *
   * @see Character#isDigit(char)
   */
  public static Parser digit() {
    return digit("digit expected");
  }

  public static Parser digit(String message) {
    return new CharParser(digitPredicate(), message);
  }

  private static CharPredicate digitPredicate() {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isDigit(input);
      }
    };
  }

  /**
   * Returns a parser that parses a single letter.
   *
   * @see Character#isLetter(char)
   */
  public static Parser letter() {
    return letter("letter expected");
  }

  public static Parser letter(String message) {
    return new CharParser(letterPredicate(), message);
  }

  private static CharPredicate letterPredicate() {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLetter(input);
      }
    };
  }

  /**
   * Returns a parser that parses an lower-case letter.
   *
   * @see Character#isLowerCase(char)
   */
  public static Parser lowerCase() {
    return lowerCase("lowercase letter expected");
  }

  public static Parser lowerCase(String message) {
    return new CharParser(lowerCasePredicate(), message);
  }

  private static CharPredicate lowerCasePredicate() {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLowerCase(input);
      }
    };
  }



  /**
   * Returns a parser that parses a specific character pattern.
   *
   * Characters match themselves. A dash @code{-} between two
   * characters matches the range of those characters. A caret
   * @code{^} at the beginning negates the pattern.
   */
  public static Parser pattern(String pattern) {
    return pattern(pattern, pattern + " expected");
  }

  public static Parser pattern(String pattern, String message) {
    return new CharParser(patternPredicate(pattern), message);
  }

  private static CharPredicate patternPredicate(String pattern) {
    return PATTERN.parse(new Context(pattern)).get();
  }

  private static CharPredicate orPredicate(final CharPredicate... predicates) {
    if (predicates.length == 1) {
      return predicates[0];
    } else {
      return new CharPredicate() {
        @Override
        public boolean apply(char input) {
          for (CharPredicate predicate : predicates) {
            if (predicate.apply(input)) {
              return true;
            }
          }
          return false;
        }
      };
    }
  }

  private static CharPredicate notPredicate(final CharPredicate predicate) {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return !predicate.apply(input);
      }
    };
  }

  private static Parser PATTERN_SIMPLE = any().map(
      new Function<Character, CharPredicate>() {
        @Override
        public CharPredicate apply(Character character) {
          return characterPredicate(character);
        }
      });

  private static Parser PATTERN_RANGE = any().seq(character('-')).seq(any())
      .map(new Function<List<Character>, CharPredicate>() {
        @Override
        public CharPredicate apply(List<Character> characters) {
          return rangePredicate(characters.get(0), characters.get(2));
        }
      });

  private static Parser PATTERN_POSITIVE = PATTERN_RANGE.or(PATTERN_SIMPLE).plus()
      .map(new Function<List<CharPredicate>, CharPredicate>() {
        @Override
        public CharPredicate apply(final List<CharPredicate> predicates) {
          return orPredicate(predicates.toArray(new CharPredicate[predicates.size()]));
        }
      });

  private static Parser PATTERN = character('^').optional().seq(PATTERN_POSITIVE)
      .map(new Function<List<CharPredicate>, CharPredicate>() {
        @Override
        public CharPredicate apply(final List<CharPredicate> predicates) {
          return predicates.get(0) == null ? predicates.get(1) : notPredicate(predicates.get(1));
        }
      }).end();

  /**
   * Returns a parser that parses a specific character range.
   */
  public static Parser range(char start, char stop) {
    return range(start, stop, start + ".." + stop + " expected");
  }

  public static Parser range(char start, char stop, String message) {
    return new CharParser(rangePredicate(start, stop), message);
  }

  private static CharPredicate rangePredicate(final char start, final char stop) {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return start <= input && input <= stop;
      }
    };
  }

  /**
   * Returns a parser that parses an upper-case letter.
   *
   * @see Character#isUpperCase(char)
   */
  public static Parser upperCase() {
    return upperCase("uppercase letter expected");
  }

  public static Parser upperCase(String message) {
    return new CharParser(upperCasePredicate(), message);
  }

  private static CharPredicate upperCasePredicate() {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isUpperCase(input);
      }
    };
  }

  /**
   * Returns a parser that parses a single whitespace.
   *
   * @see Character#isWhitespace(char)
   */
  public static Parser whitespace() {
    return whitespace("whitespace expected");
  }

  public static Parser whitespace(String message) {
    return new CharParser(whitespacePredicate(), message);
  }

  private static CharPredicate whitespacePredicate() {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isWhitespace(input);
      }
    };
  }

  /**
   * Returns a parser that parses a single letter or digit.
   *
   * @see Character#isLetterOrDigit(char)
   */
  public static Parser word() {
    return word("letter or digit expected");
  }

  public static Parser word(String message) {
    return new CharParser(wordPredicate(), message);
  }

  private static CharPredicate wordPredicate() {
    return new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLetterOrDigit(input);
      }
    };
  }

}
