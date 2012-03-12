package org.petitparser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.petitparser.context.Context;
import org.petitparser.parser.CharParser;
import org.petitparser.parser.CharParser.CharPredicate;
import org.petitparser.parser.Parser;
import org.petitparser.utils.Function;

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
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return true;
      }
    }, "character expected");
  }

  /**
   * Returns a parser that parses a specific {@code character}.
   */
  public static Parser character(final char character) {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return character == input;
      }
    }, character + " expected");
  }

  /**
   * Returns a parser that parses a single digit.
   */
  public static Parser digit() {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isDigit(input);
      }
    }, "digit expected");
  }

  /**
   * Returns a parser that parses a single letter.
   */
  public static Parser letter() {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLetter(input);
      }
    }, "letter expected");
  }

  /**
   * Returns a parser that parses a single letter or digit.
   */
  public static Parser word() {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLetterOrDigit(input);
      }
    }, "letter or digit expected");
  }

  /**
   * Returns a parser that parses an lower-case letter.
   */
  public static Parser lowerCase() {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isLowerCase(input);
      }
    }, "lowercase letter expected");
  }

  /**
   * Returns a parser that parses an upper-case letter.
   */
  public static Parser upperCase() {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isUpperCase(input);
      }
    }, "uppercase letter expected");
  }

  /**
   * Returns a parser that parses a single whitespace.
   */
  public static Parser whitespace() {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Character.isWhitespace(input);
      }
    }, "whitespace expected");
  }

  /**
   * Returns a parser that parses a specific character range.
   */
  public static Parser range(final char start, final char stop) {
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return start <= input && input <= stop;
      }
    }, start + ".." + stop + " expected");
  }

  private static Parser PATTERN_SIMPLE = any()
      .map(new Function<Character, Set<Integer>>() {
        @Override
        public Set<Integer> apply(Character argument) {
          Set<Integer> result = new TreeSet<Integer>();
          result.add((int) argument);
          return result;
        }
      });

  private static Parser PATTERN_RANGE = any().seq(character('-')).seq(any())
      .map(new Function<List<Character>, Set<Integer>>() {
        @Override
        public Set<Integer> apply(List<Character> argument) {
          Set<Integer> result = new TreeSet<Integer>();
          for (int c = argument.get(0); c <= argument.get(2); c++) {
            result.add(c);
          }
          return result;
        }
      });

  private static Parser PATTERN = PATTERN_RANGE.or(PATTERN_SIMPLE).plus()
      .map(new Function<List<Set<Integer>>, Set<Integer>>() {
        @Override
        public Set<Integer> apply(List<Set<Integer>> argument) {
          Set<Integer> result = new TreeSet<Integer>();
          for (Set<Integer> set : argument) {
            result.addAll(set);
          }
          return result;
        }
      }).end();

  /**
   * Returns a parser that parses a specific character pattern.
   */
  public static Parser pattern(String pattern) {
    Set<Integer> set = PATTERN.parse(new Context(pattern)).get();
    Iterator<Integer> iterator = set.iterator();
    final int[] array = new int[set.size()];
    for (int i = 0; i < array.length; i++) {
      array[i] = iterator.next();
    }
    return new CharParser(new CharPredicate() {
      @Override
      public boolean apply(char input) {
        return Arrays.binarySearch(array, input) >= 0;
      }
    }, pattern + " expected");
  }

}
