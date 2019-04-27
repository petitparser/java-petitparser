package org.petitparser;

import org.junit.Test;
import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.context.Token;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.SettableParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.parser.repeating.RepeatingParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.petitparser.Assertions.assertFailure;
import static org.petitparser.Assertions.assertSuccess;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests {@link Parser} factory methods.
 */
public class ParsersTest {

  @Test
  public void testAnd() {
    Parser parser = of('a').and();
    assertSuccess(parser, "a", 'a', 0);
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "");
  }

  @Test
  public void testChoice2() {
    Parser parser = of('a').or(of('b'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "c");
    assertFailure(parser, "");
  }

  @Test
  public void testChoice3() {
    Parser parser = of('a').or(of('b')).or(of('c'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
    assertFailure(parser, "");
  }

  @Test
  public void testEndOfInput() {
    Parser parser = of('a').end();
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "aa", 1, "end of input expected");
  }

  @Test
  public void testSettable() {
    SettableParser parser = of('a').settable();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", 0, "'a' expected");
    parser.set(of('b'));
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "a", 0, "'b' expected");
  }

  @Test
  public void testFlatten() {
    Parser parser = CharacterParser.digit().plus().flatten();
    assertFailure(parser, "");
    assertFailure(parser, "a");
    assertSuccess(parser, "1", "1");
    assertSuccess(parser, "12", "12");
    assertSuccess(parser, "123", "123");
    assertSuccess(parser, "1234", "1234");
  }

  @Test
  public void testMap() {
    Parser parser = CharacterParser.digit()
        .map((Function<Character, Integer>) Character::getNumericValue);
    assertSuccess(parser, "1", 1);
    assertSuccess(parser, "4", 4);
    assertSuccess(parser, "9", 9);
    assertFailure(parser, "");
    assertFailure(parser, "a");
  }

  @Test
  public void testPick() {
    Parser parser =
        CharacterParser.digit().seq(CharacterParser.letter()).pick(1);
    assertSuccess(parser, "1a", 'a');
    assertSuccess(parser, "2b", 'b');
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testPickLast() {
    Parser parser =
        CharacterParser.digit().seq(CharacterParser.letter()).pick(-1);
    assertSuccess(parser, "1a", 'a');
    assertSuccess(parser, "2b", 'b');
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testPermute() {
    Parser parser =
        CharacterParser.digit().seq(CharacterParser.letter()).permute(1, 0);
    assertSuccess(parser, "1a", Arrays.asList('a', '1'));
    assertSuccess(parser, "2b", Arrays.asList('b', '2'));
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testPermuteLast() {
    Parser parser =
        CharacterParser.digit().seq(CharacterParser.letter()).permute(-1, 0);
    assertSuccess(parser, "1a", Arrays.asList('a', '1'));
    assertSuccess(parser, "2b", Arrays.asList('b', '2'));
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  public void testNeg1() {
    Parser parser = CharacterParser.digit().neg();
    assertFailure(parser, "1", 0);
    assertFailure(parser, "9", 0);
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "", 0);
  }

  @Test
  public void testNeg2() {
    Parser parser = CharacterParser.digit().neg("no digit expected");
    assertFailure(parser, "1", 0, "no digit expected");
    assertFailure(parser, "9", 0, "no digit expected");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "", 0, "no digit expected");
  }

  @Test
  public void testNeg3() {
    Parser parser = StringParser.of("foo").neg("no foo expected");
    assertFailure(parser, "foo", 0, "no foo expected");
    assertFailure(parser, "foobar", 0, "no foo expected");
    assertSuccess(parser, "f", 'f');
    assertSuccess(parser, " ", ' ');
  }

  @Test
  public void testNot() {
    Parser parser = of('a').not("not a expected");
    assertFailure(parser, "a", "not a expected");
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testOptional() {
    Parser parser = of('a').optional();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  public void testPlus() {
    Parser parser = of('a').plus();
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  @Test
  public void testPlusGreedy() {
    Parser parser = CharacterParser.word().plusGreedy(CharacterParser.digit());
    assertFailure(parser, "", 0, "letter or digit expected");
    assertFailure(parser, "a", 1, "digit expected");
    assertFailure(parser, "ab", 1, "digit expected");
    assertFailure(parser, "1", 1, "digit expected");
    assertSuccess(parser, "a1", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "12", Arrays.asList('1'), 1);
    assertSuccess(parser, "a12", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b', '1'), 3);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c', '1'), 4);
    assertSuccess(parser, "123", Arrays.asList('1', '2'), 2);
    assertSuccess(parser, "a123", Arrays.asList('a', '1', '2'), 3);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b', '1', '2'), 4);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c', '1', '2'), 5);
  }

  @Test
  public void testPlusLazy() {
    Parser parser = CharacterParser.word().plusLazy(CharacterParser.digit());
    assertFailure(parser, "");
    assertFailure(parser, "a", 1, "digit expected");
    assertFailure(parser, "ab", 2, "digit expected");
    assertFailure(parser, "1", 1, "digit expected");
    assertSuccess(parser, "a1", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "12", Arrays.asList('1'), 1);
    assertSuccess(parser, "a12", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "123", Arrays.asList('1'), 1);
    assertSuccess(parser, "a123", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c'), 3);
  }

  @Test
  public void testTimes() {
    Parser parser = of('a').times(2);
    assertFailure(parser, "", 0, "'a' expected");
    assertFailure(parser, "a", 1, "'a' expected");
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a'), 2);
  }

  @Test
  public void testRepeat() {
    Parser parser = of('a').repeat(2, 3);
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "a", 1, "'a' expected");
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
    assertSuccess(parser, "aaaa", Arrays.asList('a', 'a', 'a'), 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRepeatMinError1() {
    of('a').repeat(-2, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRepeatMinError2() {
    of('a').repeat(3, 2);
  }

  @Test
  public void testRepeatUnbounded() {
    StringBuilder builder = new StringBuilder();
    List<Character> list = new ArrayList<>();
    for (int i = 0; i < 100000; i++) {
      builder.append('a');
      list.add('a');
    }
    Parser parser = of('a').repeat(2, RepeatingParser.UNBOUNDED);
    assertSuccess(parser, builder.toString(), list);
  }

  @Test
  public void testRepeatGreedy() {
    Parser parser =
        CharacterParser.word().repeatGreedy(CharacterParser.digit(), 2, 4);
    assertFailure(parser, "", 0, "letter or digit expected");
    assertFailure(parser, "a", 1, "letter or digit expected");
    assertFailure(parser, "ab", 2, "digit expected");
    assertFailure(parser, "abc", 2, "digit expected");
    assertFailure(parser, "abcd", 2, "digit expected");
    assertFailure(parser, "abcde", 2, "digit expected");
    assertFailure(parser, "1", 1, "letter or digit expected");
    assertFailure(parser, "a1", 2, "digit expected");
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "abcd1", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde1", 2, "digit expected");
    assertFailure(parser, "12", 2, "digit expected");
    assertSuccess(parser, "a12", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b', '1'), 3);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c', '1'), 4);
    assertSuccess(parser, "abcd12", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde12", 2, "digit expected");
    assertSuccess(parser, "123", Arrays.asList('1', '2'), 2);
    assertSuccess(parser, "a123", Arrays.asList('a', '1', '2'), 3);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b', '1', '2'), 4);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c', '1'), 4);
    assertSuccess(parser, "abcd123", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde123", 2, "digit expected");
  }

  @Test
  public void testRepeatGreedyUnbounded() {
    StringBuilder builderLetter = new StringBuilder(), builderDigit =
        new StringBuilder();
    List<Character> listLetter = new ArrayList<>(), listDigit =
        new ArrayList<>();
    for (int i = 0; i < 100000; i++) {
      builderLetter.append('a');
      listLetter.add('a');
      builderDigit.append('1');
      listDigit.add('1');
    }
    builderLetter.append('1');
    builderDigit.append('1');
    Parser parser = CharacterParser.word()
        .repeatGreedy(CharacterParser.digit(), 2, RepeatingParser.UNBOUNDED);
    assertSuccess(parser, builderLetter.toString(), listLetter,
        listLetter.size());
    assertSuccess(parser, builderDigit.toString(), listDigit, listDigit.size());
  }

  @Test
  public void testRepeatLazy() {
    Parser parser =
        CharacterParser.word().repeatLazy(CharacterParser.digit(), 2, 4);
    assertFailure(parser, "", 0, "letter or digit expected");
    assertFailure(parser, "a", 1, "letter or digit expected");
    assertFailure(parser, "ab", 2, "digit expected");
    assertFailure(parser, "abc", 3, "digit expected");
    assertFailure(parser, "abcd", 4, "digit expected");
    assertFailure(parser, "abcde", 4, "digit expected");
    assertFailure(parser, "1", 1, "letter or digit expected");
    assertFailure(parser, "a1", 2, "digit expected");
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "abcd1", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde1", 4, "digit expected");
    assertFailure(parser, "12", 2, "digit expected");
    assertSuccess(parser, "a12", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "abcd12", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde12", 4, "digit expected");
    assertSuccess(parser, "123", Arrays.asList('1', '2'), 2);
    assertSuccess(parser, "a123", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "abcd123", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde123", 4, "digit expected");
  }

  @Test
  public void testRepeatLazyUnbounded() {
    StringBuilder builder = new StringBuilder();
    List<Character> list = new ArrayList<>();
    for (int i = 0; i < 100000; i++) {
      builder.append('a');
      list.add('a');
    }
    builder.append("1111");
    Parser parser = CharacterParser.word()
        .repeatLazy(CharacterParser.digit(), 2, RepeatingParser.UNBOUNDED);
    assertSuccess(parser, builder.toString(), list, list.size());
  }

  @Test
  public void testSequence2() {
    Parser parser = of('a').seq(of('b'));
    assertSuccess(parser, "ab", Arrays.asList('a', 'b'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
  }

  @Test
  public void testSequence3() {
    Parser parser = of('a').seq(of('b')).seq(of('c'));
    assertSuccess(parser, "abc", Arrays.asList('a', 'b', 'c'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
    assertFailure(parser, "ab", 2);
    assertFailure(parser, "abx", 2);
  }

  @Test
  public void testStar() {
    Parser parser = of('a').star();
    assertSuccess(parser, "", Arrays.asList());
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  @Test
  public void testStarGreedy() {
    Parser parser = CharacterParser.word().starGreedy(CharacterParser.digit());
    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "a", 0, "digit expected");
    assertFailure(parser, "ab", 0, "digit expected");
    assertSuccess(parser, "1", Arrays.asList(), 0);
    assertSuccess(parser, "a1", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "12", Arrays.asList('1'), 1);
    assertSuccess(parser, "a12", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b', '1'), 3);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c', '1'), 4);
    assertSuccess(parser, "123", Arrays.asList('1', '2'), 2);
    assertSuccess(parser, "a123", Arrays.asList('a', '1', '2'), 3);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b', '1', '2'), 4);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c', '1', '2'), 5);
  }

  @Test
  public void testStarLazy() {
    Parser parser = CharacterParser.word().starLazy(CharacterParser.digit());
    assertFailure(parser, "");
    assertFailure(parser, "a", 1, "digit expected");
    assertFailure(parser, "ab", 2, "digit expected");
    assertSuccess(parser, "1", Arrays.asList(), 0);
    assertSuccess(parser, "a1", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "12", Arrays.asList(), 0);
    assertSuccess(parser, "a12", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "123", Arrays.asList(), 0);
    assertSuccess(parser, "a123", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c'), 3);
  }

  @Test
  public void testToken() {
    Parser parser = of('a').star().token().trim();
    Token token = parser.parse(" aa ").get();
    assertEquals(1, token.getStart());
    assertEquals(3, token.getStop());
    assertEquals(Arrays.asList('a', 'a'), token.<List<Character>>getValue());
  }

  @Test
  public void testTrim() {
    Parser parser = of('a').trim();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " a", 'a');
    assertSuccess(parser, "a ", 'a');
    assertSuccess(parser, " a ", 'a');
    assertSuccess(parser, "  a", 'a');
    assertSuccess(parser, "a  ", 'a');
    assertSuccess(parser, "  a  ", 'a');
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, " b", 1, "'a' expected");
    assertFailure(parser, "  b", 2, "'a' expected");
  }

  @Test
  public void testTrimCustom() {
    Parser parser = of('a').trim(of('*'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "*a", 'a');
    assertSuccess(parser, "a*", 'a');
    assertSuccess(parser, "*a*", 'a');
    assertSuccess(parser, "**a", 'a');
    assertSuccess(parser, "a**", 'a');
    assertSuccess(parser, "**a**", 'a');
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "*b", 1, "'a' expected");
    assertFailure(parser, "**b", 2, "'a' expected");
  }

  @Test
  public void testSeparatedBy() {
    Parser parser = of('a').separatedBy(of('b'));
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "ab", Arrays.asList('a'), 1);
    assertSuccess(parser, "aba", Arrays.asList('a', 'b', 'a'));
    assertSuccess(parser, "abab", Arrays.asList('a', 'b', 'a'), 3);
    assertSuccess(parser, "ababa", Arrays.asList('a', 'b', 'a', 'b', 'a'));
    assertSuccess(parser, "ababab", Arrays.asList('a', 'b', 'a', 'b', 'a'), 5);
  }

  @Test
  public void testDelimitedBy() {
    Parser parser = of('a').delimitedBy(of('b'));
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "ab", Arrays.asList('a', 'b'));
    assertSuccess(parser, "aba", Arrays.asList('a', 'b', 'a'));
    assertSuccess(parser, "abab", Arrays.asList('a', 'b', 'a', 'b'));
    assertSuccess(parser, "ababa", Arrays.asList('a', 'b', 'a', 'b', 'a'));
    assertSuccess(parser, "ababab",
        Arrays.asList('a', 'b', 'a', 'b', 'a', 'b'));
  }

  @Test
  public void testContinuationDelegating() {
    Parser parser = CharacterParser.digit().callCC(Function::apply);
    assertTrue(parser.parse("1").isSuccess());
    assertFalse(parser.parse("a").isSuccess());
  }

  @Test
  public void testContinuationRedirecting() {
    Parser parser = CharacterParser.digit().callCC(
        (continuation, context) -> CharacterParser.letter().parseOn(context));
    assertFalse(parser.parse("1").isSuccess());
    assertTrue(parser.parse("a").isSuccess());
  }

  @Test
  public void testContinuationResuming() {
    List<Function<Context, Result>> continuations = new ArrayList<>();
    List<Context> contexts = new ArrayList<>();
    Parser parser = CharacterParser.digit().callCC((continuation, context) -> {
      continuations.add(continuation);
      contexts.add(context);
      // we have to return something for now
      return context.failure("Abort");
    });
    // execute the parser twice to collect the continuations
    assertFalse(parser.parse("1").isSuccess());
    assertFalse(parser.parse("a").isSuccess());
    // later we can execute the captured continuations
    assertTrue(continuations.get(0).apply(contexts.get(0)).isSuccess());
    assertFalse(continuations.get(1).apply(contexts.get(1)).isSuccess());
    // of course the continuations can be resumed multiple times
    assertTrue(continuations.get(0).apply(contexts.get(0)).isSuccess());
    assertFalse(continuations.get(1).apply(contexts.get(1)).isSuccess());
  }

  @Test
  public void testContinuationSuccessful() {
    Parser parser = CharacterParser.digit()
        .callCC((continuation, context) -> context.success("Always succeed"));
    assertTrue(parser.parse("1").isSuccess());
    assertTrue(parser.parse("a").isSuccess());
  }

  @Test
  public void testContinuationFailing() {
    Parser parser = CharacterParser.digit()
        .callCC((continuation, context) -> context.failure("Always fail"));
    assertFalse(parser.parse("1").isSuccess());
    assertFalse(parser.parse("a").isSuccess());
  }
}
