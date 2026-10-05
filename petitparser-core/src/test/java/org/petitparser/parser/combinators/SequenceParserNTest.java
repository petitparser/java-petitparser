package org.petitparser.parser.combinators;

import org.junit.Test;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;
import org.petitparser.parser.repeating.Tuple2;
import org.petitparser.parser.repeating.Tuple3;
import org.petitparser.parser.repeating.Tuple4;
import org.petitparser.parser.repeating.Tuple5;
import org.petitparser.parser.repeating.Tuple6;
import org.petitparser.parser.repeating.Tuple7;
import org.petitparser.parser.repeating.Tuple8;
import org.petitparser.parser.repeating.Tuple9;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests {@link SequenceParser2} through {@link SequenceParser9}.
 */
public class SequenceParserNTest {

  @Test
  public void testSequenceParser2() {
    SequenceParser2<Character, Character> parser = of('a').then(of('b'));
    Result result = parser.parse("ab");
    assertTrue(result.isSuccess());
    Tuple2<Character, Character> tuple = result.get();
    assertEquals(Character.valueOf('a'), tuple.first());
    assertEquals(Character.valueOf('b'), tuple.second());
    assertEquals(Arrays.asList('a', 'b'), result.get());

    assertTrue(parser.accept("ab"));
    assertFalse(parser.accept("xb"));
    assertFalse(parser.accept("ax"));
    assertFalse(parser.accept(""));

    assertEquals(2, parser.fastParseOn("abc", 0));
    assertEquals(-1, parser.fastParseOn("xbc", 0));
    assertEquals(-1, parser.fastParseOn("axc", 0));

    Parser copy = parser.copy();
    assertTrue(parser.isEqualTo(copy));

    Parser mapped = parser.map((c1, c2) -> "" + c1 + c2);
    assertEquals("ab", mapped.parse("ab").get());
  }

  @Test(expected = NullPointerException.class)
  public void testSequenceParser2NullMap() {
    Parser.seq(of('a'), of('b')).map((java.util.function.BiFunction) null);
  }

  @Test
  public void testSequenceParser3() {
    SequenceParser3<Character, Character, Character> parser =
        of('a').then(of('b')).then(of('c'));
    Result result = parser.parse("abc");
    assertTrue(result.isSuccess());
    Tuple3<Character, Character, Character> tuple = result.get();
    assertEquals(Character.valueOf('a'), tuple.first());
    assertEquals(Character.valueOf('b'), tuple.second());
    assertEquals(Character.valueOf('c'), tuple.third());
    assertEquals(Arrays.asList('a', 'b', 'c'), result.get());

    assertTrue(parser.accept("abc"));
    assertFalse(parser.accept("xbc"));
    assertFalse(parser.accept("axc"));
    assertFalse(parser.accept("abx"));

    assertEquals(3, parser.fastParseOn("abcd", 0));
    assertEquals(-1, parser.fastParseOn("xbcd", 0));
    assertEquals(-1, parser.fastParseOn("axcd", 0));
    assertEquals(-1, parser.fastParseOn("abxd", 0));

    Parser copy = parser.copy();
    assertTrue(parser.isEqualTo(copy));

    Parser mapped = parser.map((c1, c2, c3) -> "" + c1 + c2 + c3);
    assertEquals("abc", mapped.parse("abc").get());
  }

  @Test(expected = NullPointerException.class)
  public void testSequenceParser3NullMap() {
    Parser.seq(of('a'), of('b'), of('c')).map((org.petitparser.utils.functions.Function3) null);
  }

  @Test
  public void testSequenceParser4() {
    SequenceParser4<Character, Character, Character, Character> parser =
        of('a').then(of('b')).then(of('c')).then(of('d'));
    Result result = parser.parse("abcd");
    assertTrue(result.isSuccess());
    Tuple4<Character, Character, Character, Character> tuple = result.get();
    assertEquals(Character.valueOf('a'), tuple.first());
    assertEquals(Character.valueOf('b'), tuple.second());
    assertEquals(Character.valueOf('c'), tuple.third());
    assertEquals(Character.valueOf('d'), tuple.fourth());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd'), result.get());

    assertTrue(parser.accept("abcd"));
    assertFalse(parser.accept("xbcd"));
    assertFalse(parser.accept("axcd"));
    assertFalse(parser.accept("abxd"));
    assertFalse(parser.accept("abcx"));

    assertEquals(4, parser.fastParseOn("abcde", 0));
    assertEquals(-1, parser.fastParseOn("xbcde", 0));
    assertEquals(-1, parser.fastParseOn("axcde", 0));
    assertEquals(-1, parser.fastParseOn("abxde", 0));
    assertEquals(-1, parser.fastParseOn("abcxe", 0));

    Parser copy = parser.copy();
    assertTrue(parser.isEqualTo(copy));

    Parser mapped = parser.map((c1, c2, c3, c4) -> "" + c1 + c2 + c3 + c4);
    assertEquals("abcd", mapped.parse("abcd").get());
  }

  @Test(expected = NullPointerException.class)
  public void testSequenceParser4NullMap() {
    Parser.seq(of('a'), of('b'), of('c'), of('d')).map((org.petitparser.utils.functions.Function4) null);
  }

  @Test
  public void testSequenceParser5() {
    SequenceParser5<Character, Character, Character, Character, Character> parser =
        of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e'));
    Result result = parser.parse("abcde");
    assertTrue(result.isSuccess());
    Tuple5<Character, Character, Character, Character, Character> tuple = result.get();
    assertEquals(Character.valueOf('a'), tuple.first());
    assertEquals(Character.valueOf('e'), tuple.fifth());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e'), result.get());

    assertTrue(parser.accept("abcde"));
    assertFalse(parser.accept("abcdx"));

    assertEquals(5, parser.fastParseOn("abcdef", 0));
    assertEquals(-1, parser.fastParseOn("xbcdef", 0));
    assertEquals(-1, parser.fastParseOn("axcdef", 0));
    assertEquals(-1, parser.fastParseOn("abxdef", 0));
    assertEquals(-1, parser.fastParseOn("abcxef", 0));
    assertEquals(-1, parser.fastParseOn("abcdxf", 0));

    Parser copy = parser.copy();
    assertTrue(parser.isEqualTo(copy));

    Parser mapped = parser.map((c1, c2, c3, c4, c5) -> "" + c1 + c2 + c3 + c4 + c5);
    assertEquals("abcde", mapped.parse("abcde").get());
  }

  @Test(expected = NullPointerException.class)
  public void testSequenceParser5NullMap() {
    Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e')).map((org.petitparser.utils.functions.Function5) null);
  }

  @Test
  public void testSequenceParser6() {
    SequenceParser6<Character, Character, Character, Character, Character, Character> parser =
        of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e')).then(of('f'));
    Result result = parser.parse("abcdef");
    assertTrue(result.isSuccess());
    Tuple6<Character, Character, Character, Character, Character, Character> tuple = result.get();
    assertEquals(Character.valueOf('a'), tuple.first());
    assertEquals(Character.valueOf('f'), tuple.sixth());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f'), result.get());

    assertTrue(parser.accept("abcdef"));
    assertFalse(parser.accept("abcdex"));

    assertEquals(6, parser.fastParseOn("abcdefg", 0));
    assertEquals(-1, parser.fastParseOn("xbcdefg", 0));
    assertEquals(-1, parser.fastParseOn("axcdefg", 0));
    assertEquals(-1, parser.fastParseOn("abxdefg", 0));
    assertEquals(-1, parser.fastParseOn("abcxefg", 0));
    assertEquals(-1, parser.fastParseOn("abcdxfg", 0));
    assertEquals(-1, parser.fastParseOn("abcdexg", 0));

    Parser copy = parser.copy();
    assertTrue(parser.isEqualTo(copy));

    Parser mapped = parser.map((c1, c2, c3, c4, c5, c6) -> "" + c1 + c2 + c3 + c4 + c5 + c6);
    assertEquals("abcdef", mapped.parse("abcdef").get());
  }

  @Test(expected = NullPointerException.class)
  public void testSequenceParser6NullMap() {
    Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e'), of('f')).map((org.petitparser.utils.functions.Function6) null);
  }

  @Test
  public void testSequenceParser7() {
    SequenceParser7<Character, Character, Character, Character, Character, Character, Character> parser =
        of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e')).then(of('f')).then(of('g'));
    Result result = parser.parse("abcdefg");
    assertTrue(result.isSuccess());
    Tuple7<Character, Character, Character, Character, Character, Character, Character> tuple = result.get();
    assertEquals(Character.valueOf('a'), tuple.first());
    assertEquals(Character.valueOf('g'), tuple.seventh());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g'), result.get());

    assertTrue(parser.accept("abcdefg"));
    assertFalse(parser.accept("abcdefx"));

    assertEquals(7, parser.fastParseOn("abcdefgh", 0));
    assertEquals(-1, parser.fastParseOn("xbcdefgh", 0));
    assertEquals(-1, parser.fastParseOn("axcdefgh", 0));
    assertEquals(-1, parser.fastParseOn("abxdefgh", 0));
    assertEquals(-1, parser.fastParseOn("abcxefgh", 0));
    assertEquals(-1, parser.fastParseOn("abcdxfgh", 0));
    assertEquals(-1, parser.fastParseOn("abcdexgh", 0));
    assertEquals(-1, parser.fastParseOn("abcdefxh", 0));

    Parser copy = parser.copy();
    assertTrue(parser.isEqualTo(copy));

    Parser mapped = parser.map((c1, c2, c3, c4, c5, c6, c7) -> "" + c1 + c2 + c3 + c4 + c5 + c6 + c7);
    assertEquals("abcdefg", mapped.parse("abcdefg").get());
  }

  @Test(expected = NullPointerException.class)
  public void testSequenceParser7NullMap() {
    Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g')).map((org.petitparser.utils.functions.Function7) null);
  }

  @Test
  public void testSequenceParser8() {
    SequenceParser8<Character, Character, Character, Character, Character, Character, Character, Character> parser =
        of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e')).then(of('f')).then(of('g')).then(of('h'));
    Result result = parser.parse("abcdefgh");
    assertTrue(result.isSuccess());
    Tuple8<Character, Character, Character, Character, Character, Character, Character, Character> tuple = result.get();
    assertEquals(Character.valueOf('a'), tuple.first());
    assertEquals(Character.valueOf('h'), tuple.eighth());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'), result.get());

    assertTrue(parser.accept("abcdefgh"));
    assertFalse(parser.accept("abcdefgx"));

    assertEquals(8, parser.fastParseOn("abcdefghi", 0));
    assertEquals(-1, parser.fastParseOn("xbcdefghi", 0));
    assertEquals(-1, parser.fastParseOn("axcdefghi", 0));
    assertEquals(-1, parser.fastParseOn("abxdefghi", 0));
    assertEquals(-1, parser.fastParseOn("abcxefghi", 0));
    assertEquals(-1, parser.fastParseOn("abcdxfghi", 0));
    assertEquals(-1, parser.fastParseOn("abcdexghi", 0));
    assertEquals(-1, parser.fastParseOn("abcdefxhi", 0));
    assertEquals(-1, parser.fastParseOn("abcdefgxi", 0));

    Parser copy = parser.copy();
    assertTrue(parser.isEqualTo(copy));

    Parser mapped = parser.map((c1, c2, c3, c4, c5, c6, c7, c8) ->
        "" + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8);
    assertEquals("abcdefgh", mapped.parse("abcdefgh").get());
  }

  @Test(expected = NullPointerException.class)
  public void testSequenceParser8NullMap() {
    Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g'), of('h')).map((org.petitparser.utils.functions.Function8) null);
  }

  @Test
  public void testSequenceParser9() {
    SequenceParser9<Character, Character, Character, Character, Character, Character, Character, Character, Character> parser =
        of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e'))
            .then(of('f')).then(of('g')).then(of('h')).then(of('i'));
    Result result = parser.parse("abcdefghi");
    assertTrue(result.isSuccess());
    Tuple9<Character, Character, Character, Character, Character, Character, Character, Character, Character> tuple = result.get();
    assertEquals(Character.valueOf('a'), tuple.first());
    assertEquals(Character.valueOf('i'), tuple.ninth());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'), result.get());

    assertTrue(parser.accept("abcdefghi"));
    assertFalse(parser.accept("abcdefghx"));

    assertEquals(9, parser.fastParseOn("abcdefghij", 0));
    assertEquals(-1, parser.fastParseOn("xbcdefghij", 0));
    assertEquals(-1, parser.fastParseOn("axcdefghij", 0));
    assertEquals(-1, parser.fastParseOn("abxdefghij", 0));
    assertEquals(-1, parser.fastParseOn("abcxefghij", 0));
    assertEquals(-1, parser.fastParseOn("abcdxfghij", 0));
    assertEquals(-1, parser.fastParseOn("abcdexghij", 0));
    assertEquals(-1, parser.fastParseOn("abcdefxhij", 0));
    assertEquals(-1, parser.fastParseOn("abcdefgxij", 0));
    assertEquals(-1, parser.fastParseOn("abcdefghxj", 0));

    Parser copy = parser.copy();
    assertTrue(parser.isEqualTo(copy));

    Parser mapped = parser.map((c1, c2, c3, c4, c5, c6, c7, c8, c9) ->
        "" + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8 + c9);
    assertEquals("abcdefghi", mapped.parse("abcdefghi").get());

    // Test then beyond 9 elements falls back to SequenceParser
    SequenceParser seq10 = parser.then(of('j'));
    assertEquals(10, seq10.getChildren().size());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'),
        seq10.parse("abcdefghij").get());
  }

  @Test(expected = NullPointerException.class)
  public void testSequenceParser9NullMap() {
    Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g'), of('h'), of('i')).map((org.petitparser.utils.functions.Function9) null);
  }

  @Test
  public void testStaticSeqFactories() {
    assertEquals(Arrays.asList('a', 'b'), Parser.seq(of('a'), of('b')).parse("ab").get());
    assertEquals(Arrays.asList('a', 'b', 'c'), Parser.seq(of('a'), of('b'), of('c')).parse("abc").get());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd'), Parser.seq(of('a'), of('b'), of('c'), of('d')).parse("abcd").get());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e'), Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e')).parse("abcde").get());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f'), Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e'), of('f')).parse("abcdef").get());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g'), Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g')).parse("abcdefg").get());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'), Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g'), of('h')).parse("abcdefgh").get());
    assertEquals(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'), Parser.seq(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g'), of('h'), of('i')).parse("abcdefghi").get());
    assertEquals(Arrays.asList('a'), new SequenceParser(of('a')).parse("a").get());
    assertEquals(Arrays.asList('a', 'b'), of('a').seq(of('b')).parse("ab").get());
  }

  @Test
  public void testDirectFluentChainingAndMapping() {
    Parser p2 = of('a').then(of('b')).map((c1, c2) -> "" + c1 + c2);
    assertEquals("ab", p2.parse("ab").get());

    Parser p3 = of('a').then(of('b')).then(of('c'))
        .map((c1, c2, c3) -> "" + c1 + c2 + c3);
    assertEquals("abc", p3.parse("abc").get());

    Parser p4 = of('a').then(of('b')).then(of('c')).then(of('d'))
        .map((c1, c2, c3, c4) -> "" + c1 + c2 + c3 + c4);
    assertEquals("abcd", p4.parse("abcd").get());

    Parser p5 = of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e'))
        .map((c1, c2, c3, c4, c5) -> "" + c1 + c2 + c3 + c4 + c5);
    assertEquals("abcde", p5.parse("abcde").get());

    Parser p6 = of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e')).then(of('f'))
        .map((c1, c2, c3, c4, c5, c6) -> "" + c1 + c2 + c3 + c4 + c5 + c6);
    assertEquals("abcdef", p6.parse("abcdef").get());

    Parser p7 = of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e')).then(of('f')).then(of('g'))
        .map((c1, c2, c3, c4, c5, c6, c7) -> "" + c1 + c2 + c3 + c4 + c5 + c6 + c7);
    assertEquals("abcdefg", p7.parse("abcdefg").get());

    Parser p8 = of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e')).then(of('f')).then(of('g')).then(of('h'))
        .map((c1, c2, c3, c4, c5, c6, c7, c8) -> "" + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8);
    assertEquals("abcdefgh", p8.parse("abcdefgh").get());

    Parser p9 = of('a').then(of('b')).then(of('c')).then(of('d')).then(of('e')).then(of('f')).then(of('g')).then(of('h')).then(of('i'))
        .map((c1, c2, c3, c4, c5, c6, c7, c8, c9) -> "" + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8 + c9);
    assertEquals("abcdefghi", p9.parse("abcdefghi").get());
  }

  @Test
  public void testUntypedSequenceParserMultiArityMap() {
    Parser p2 = new SequenceParser(of('a'), of('b')).map((c1, c2) -> "" + c1 + c2);
    assertEquals("ab", p2.parse("ab").get());

    Parser p3 = new SequenceParser(of('a'), of('b'), of('c')).map((c1, c2, c3) -> "" + c1 + c2 + c3);
    assertEquals("abc", p3.parse("abc").get());

    Parser p4 = new SequenceParser(of('a'), of('b'), of('c'), of('d'))
        .map((c1, c2, c3, c4) -> "" + c1 + c2 + c3 + c4);
    assertEquals("abcd", p4.parse("abcd").get());

    Parser p5 = new SequenceParser(of('a'), of('b'), of('c'), of('d'), of('e'))
        .map((c1, c2, c3, c4, c5) -> "" + c1 + c2 + c3 + c4 + c5);
    assertEquals("abcde", p5.parse("abcde").get());

    Parser p6 = new SequenceParser(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'))
        .map((c1, c2, c3, c4, c5, c6) -> "" + c1 + c2 + c3 + c4 + c5 + c6);
    assertEquals("abcdef", p6.parse("abcdef").get());

    Parser p7 = new SequenceParser(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g'))
        .map((c1, c2, c3, c4, c5, c6, c7) -> "" + c1 + c2 + c3 + c4 + c5 + c6 + c7);
    assertEquals("abcdefg", p7.parse("abcdefg").get());

    Parser p8 = new SequenceParser(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g'), of('h'))
        .map((c1, c2, c3, c4, c5, c6, c7, c8) -> "" + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8);
    assertEquals("abcdefgh", p8.parse("abcdefgh").get());

    Parser p9 = new SequenceParser(of('a'), of('b'), of('c'), of('d'), of('e'), of('f'), of('g'), of('h'), of('i'))
        .map((c1, c2, c3, c4, c5, c6, c7, c8, c9) -> "" + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8 + c9);
    assertEquals("abcdefghi", p9.parse("abcdefghi").get());
  }

  @Test(expected = NullPointerException.class)
  public void testNullBiFunctionMap() {
    of('a').map((java.util.function.BiFunction) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullFunction4Map() {
    of('a').map((org.petitparser.utils.functions.Function4) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullFunction5Map() {
    of('a').map((org.petitparser.utils.functions.Function5) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullFunction6Map() {
    of('a').map((org.petitparser.utils.functions.Function6) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullFunction7Map() {
    of('a').map((org.petitparser.utils.functions.Function7) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullFunction3Map() {
    of('a').map((org.petitparser.utils.functions.Function3) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullFunction8Map() {
    of('a').map((org.petitparser.utils.functions.Function8) null);
  }

  @Test(expected = NullPointerException.class)
  public void testNullFunction9Map() {
    of('a').map((org.petitparser.utils.functions.Function9) null);
  }
}
