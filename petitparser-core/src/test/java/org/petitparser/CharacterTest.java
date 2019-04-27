package org.petitparser;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;

import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.any;
import static org.petitparser.parser.primitive.CharacterParser.anyOf;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;
import static org.petitparser.parser.primitive.CharacterParser.lowerCase;
import static org.petitparser.parser.primitive.CharacterParser.none;
import static org.petitparser.parser.primitive.CharacterParser.noneOf;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.CharacterParser.pattern;
import static org.petitparser.parser.primitive.CharacterParser.range;
import static org.petitparser.parser.primitive.CharacterParser.upperCase;
import static org.petitparser.parser.primitive.CharacterParser.whitespace;
import static org.petitparser.parser.primitive.CharacterParser.word;

/**
 * Tests {@link CharacterParser}.
 */
public class CharacterTest {

  @Test
  public void testAny() {
    Parser parser = any();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "any character expected");
  }

  @Test
  public void testAnyWithMessage() {
    Parser parser = any("wrong");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testAnyOf() {
    Parser parser = anyOf("uncopyrightable");
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "g", 'g');
    assertSuccess(parser, "h", 'h');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertSuccess(parser, "p", 'p');
    assertSuccess(parser, "r", 'r');
    assertSuccess(parser, "t", 't');
    assertSuccess(parser, "y", 'y');
    assertFailure(parser, "x", "any of 'uncopyrightable' expected");
  }

  @Test
  public void testAnyOfWithMessage() {
    Parser parser = anyOf("uncopyrightable", "wrong");
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "g", 'g');
    assertSuccess(parser, "h", 'h');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertSuccess(parser, "p", 'p');
    assertSuccess(parser, "r", 'r');
    assertSuccess(parser, "t", 't');
    assertSuccess(parser, "y", 'y');
    assertFailure(parser, "x", "wrong");
  }

  @Test
  public void testAnyOfEmpty() {
    Parser parser = anyOf("");
    assertFailure(parser, "a", "any of '' expected");
    assertFailure(parser, "b", "any of '' expected");
    assertFailure(parser, "", "any of '' expected");
  }

  @Test
  public void testNone() {
    Parser parser = none();
    assertFailure(parser, "a", "no character expected");
    assertFailure(parser, "b", "no character expected");
    assertFailure(parser, "", "no character expected");
  }

  @Test
  public void testNoneWithMessage() {
    Parser parser = none("wrong");
    assertFailure(parser, "a", "wrong");
    assertFailure(parser, "b", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testNoneOf() {
    Parser parser = noneOf("uncopyrightable");
    assertSuccess(parser, "x", 'x');
    assertFailure(parser, "c", "none of 'uncopyrightable' expected");
    assertFailure(parser, "g", "none of 'uncopyrightable' expected");
    assertFailure(parser, "h", "none of 'uncopyrightable' expected");
    assertFailure(parser, "i", "none of 'uncopyrightable' expected");
    assertFailure(parser, "o", "none of 'uncopyrightable' expected");
    assertFailure(parser, "p", "none of 'uncopyrightable' expected");
    assertFailure(parser, "r", "none of 'uncopyrightable' expected");
    assertFailure(parser, "t", "none of 'uncopyrightable' expected");
    assertFailure(parser, "y", "none of 'uncopyrightable' expected");
  }

  @Test
  public void testNoneOfWithMessage() {
    Parser parser = noneOf("uncopyrightable", "wrong");
    assertSuccess(parser, "x", 'x');
    assertFailure(parser, "c", "wrong");
    assertFailure(parser, "g", "wrong");
    assertFailure(parser, "h", "wrong");
    assertFailure(parser, "i", "wrong");
    assertFailure(parser, "o", "wrong");
    assertFailure(parser, "p", "wrong");
    assertFailure(parser, "r", "wrong");
    assertFailure(parser, "t", "wrong");
    assertFailure(parser, "y", "wrong");
  }

  @Test
  public void testNoneOfEmpty() {
    Parser parser = noneOf("");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "none of '' expected");
  }

  @Test
  public void testIs() {
    Parser parser = of('a');
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "", "'a' expected");
  }

  @Test
  public void testIsWithMessage() {
    Parser parser = of('a', "wrong");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testDigit() {
    Parser parser = digit();
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "9", '9');
    assertFailure(parser, "a", "digit expected");
    assertFailure(parser, "", "digit expected");
  }

  @Test
  public void testDigitWithMessage() {
    Parser parser = digit("wrong");
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "9", '9');
    assertFailure(parser, "a", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testLetter() {
    Parser parser = letter();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "X", 'X');
    assertFailure(parser, "0", "letter expected");
    assertFailure(parser, "", "letter expected");
  }

  @Test
  public void testLetterWithMessage() {
    Parser parser = letter("wrong");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "X", 'X');
    assertFailure(parser, "0", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testLowerCase() {
    Parser parser = lowerCase();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "A", "lowercase letter expected");
    assertFailure(parser, "0", "lowercase letter expected");
    assertFailure(parser, "", "lowercase letter expected");
  }

  @Test
  public void testLowerCaseWithMessage() {
    Parser parser = lowerCase("wrong");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "A", "wrong");
    assertFailure(parser, "0", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testPatternWithSingle() {
    Parser parser = pattern("abc");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d", "[abc] expected");
    assertFailure(parser, "", "[abc] expected");
  }

  @Test
  public void testPatternWithMessage() {
    Parser parser = pattern("abc", "wrong");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "d", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testPatternWithRange() {
    Parser parser = pattern("a-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d", "[a-c] expected");
    assertFailure(parser, "", "[a-c] expected");
  }

  @Test
  public void testPatternWithOverlappingRange() {
    Parser parser = pattern("b-da-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertFailure(parser, "e", "[b-da-c] expected");
    assertFailure(parser, "", "[b-da-c] expected");
  }

  @Test
  public void testPatternWithAdjacentRange() {
    Parser parser = pattern("c-ea-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "e", 'e');
    assertFailure(parser, "f", "[c-ea-c] expected");
    assertFailure(parser, "", "[c-ea-c] expected");
  }

  @Test
  public void testPatternWithPrefixRange() {
    Parser parser = pattern("a-ea-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "e", 'e');
    assertFailure(parser, "f", "[a-ea-c] expected");
    assertFailure(parser, "", "[a-ea-c] expected");
  }

  @Test
  public void testPatternWithPostfixRange() {
    Parser parser = pattern("a-ec-e");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "e", 'e');
    assertFailure(parser, "f", "[a-ec-e] expected");
    assertFailure(parser, "", "[a-ec-e] expected");
  }

  @Test
  public void testPatternWithRepeatedRange() {
    Parser parser = pattern("a-ea-e");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "e", 'e');
    assertFailure(parser, "f", "[a-ea-e] expected");
    assertFailure(parser, "", "[a-ea-e] expected");
  }

  @Test
  public void testPatternWithComposed() {
    Parser parser = pattern("ac-df-");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "f", 'f');
    assertSuccess(parser, "-", '-');
    assertFailure(parser, "b", "[ac-df-] expected");
    assertFailure(parser, "e", "[ac-df-] expected");
    assertFailure(parser, "g", "[ac-df-] expected");
    assertFailure(parser, "", "[ac-df-] expected");
  }

  @Test
  public void testPatternWithNegatedSingle() {
    Parser parser = pattern("^a");
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "a", "[^a] expected");
    assertFailure(parser, "", "[^a] expected");
  }

  @Test
  public void testPatternWithNegatedRange() {
    Parser parser = pattern("^a-c");
    assertSuccess(parser, "d", 'd');
    assertFailure(parser, "a", "[^a-c] expected");
    assertFailure(parser, "b", "[^a-c] expected");
    assertFailure(parser, "c", "[^a-c] expected");
    assertFailure(parser, "", "[^a-c] expected");
  }

  @Test
  public void testRange() {
    Parser parser = range('e', 'o');
    assertFailure(parser, "d", "e..o expected");
    assertSuccess(parser, "e", 'e');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertFailure(parser, "p", "e..o expected");
    assertFailure(parser, "", "e..o expected");
  }

  @Test
  public void testRangeWithMessage() {
    Parser parser = range('e', 'o', "wrong");
    assertFailure(parser, "d", "wrong");
    assertSuccess(parser, "e", 'e');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertFailure(parser, "p", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testUpperCase() {
    Parser parser = upperCase();
    assertSuccess(parser, "Z", 'Z');
    assertFailure(parser, "z", "uppercase letter expected");
    assertFailure(parser, "0", "uppercase letter expected");
    assertFailure(parser, "", "uppercase letter expected");
  }

  @Test
  public void testUpperCaseWithMessage() {
    Parser parser = upperCase("wrong");
    assertSuccess(parser, "Z", 'Z');
    assertFailure(parser, "z", "wrong");
    assertFailure(parser, "0", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testWhitespace() {
    Parser parser = whitespace();
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "z", "whitespace expected");
    assertFailure(parser, "-", "whitespace expected");
    assertFailure(parser, "", "whitespace expected");
  }

  @Test
  public void testWhitespaceWithMessage() {
    Parser parser = whitespace("wrong");
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "z", "wrong");
    assertFailure(parser, "-", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  public void testWord() {
    Parser parser = word();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "0", '0');
    assertFailure(parser, "-", "letter or digit expected");
    assertFailure(parser, "", "letter or digit expected");
  }

  @Test
  public void testWordWithMessage() {
    Parser parser = word("wrong");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "0", '0');
    assertFailure(parser, "-", "wrong");
    assertFailure(parser, "", "wrong");
  }
}
