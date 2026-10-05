package org.petitparser;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.EpsilonParser;
import org.petitparser.parser.primitive.FailureParser;
import org.petitparser.parser.primitive.StringParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.petitparser.parser.primitive.CharacterParser.of;

/**
 * Tests {@link Parser#copy}, {@link Parser#equals(Object)}, and {@link
 * Parser#replace(Parser, Parser)}.
 */
public class EqualityTest {

  private void verify(Parser parser) {
    Parser copy = parser.copy();
    // check copying
    assertNotSame(parser, copy);
    assertEquals(parser.getClass(), copy.getClass());
    assertEquals(parser.getChildren().size(), copy.getChildren().size());
    assertPairwiseSame(parser.getChildren(), copy.getChildren());
    assertEquals(parser.toString(), copy.toString());
    // check equality
    assertTrue(copy.isEqualTo(copy));
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));
    assertTrue(parser.isEqualTo(parser));
    // check replacing
    List<Parser> replaced = new ArrayList<>();
    for (int i = 0; i < copy.getChildren().size(); i++) {
      Parser source = copy.getChildren().get(i);
      Parser target = CharacterParser.any();
      copy.replace(source, target);
      assertSame(target, copy.getChildren().get(i));
      replaced.add(target);
    }
    assertPairwiseSame(replaced, copy.getChildren());
  }

  private void assertPairwiseSame(List<Parser> expected, List<Parser> actual) {
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertSame(expected.get(i), actual.get(i));
    }
  }

  @Test
  public void differentChildren() {
    Parser first = of('a').or(of('b')).or(of('c'));
    Parser second = of('a').or(of('b')).or(of('d'));
    assertFalse(first.isEqualTo(second));
    assertFalse(second.isEqualTo(first));
  }

  @Test
  public void differentSize() {
    Parser first = of('a').or(of('b')).or(of('c'));
    Parser second = of('a').or(of('b'));
    assertFalse(first.isEqualTo(second));
    assertFalse(second.isEqualTo(first));
  }

  @Test
  public void any() {
    verify(CharacterParser.any());
  }

  @Test
  public void and() {
    verify(CharacterParser.digit().and());
  }

  @Test
  public void is() {
    verify(CharacterParser.of('a'));
  }

  @Test
  public void digit() {
    verify(CharacterParser.digit());
  }

  @Test
  public void delegate() {
    verify(new DelegateParser(CharacterParser.any()));
  }

  @Test
  public void continuation() {
    verify(CharacterParser.digit().callCC((continuation, context) -> null));
  }

  @Test
  public void end() {
    verify(CharacterParser.digit().end());
  }

  @Test
  public void epsilon() {
    verify(new EpsilonParser());
  }

  @Test
  public void epsilonWithValue() {
    verify(new EpsilonParser(42));
  }

  @Test
  public void failure() {
    verify(FailureParser.withMessage("failure"));
  }

  @Test
  public void flatten1() {
    verify(CharacterParser.digit().flatten());
  }

  @Test
  public void flatten2() {
    verify(CharacterParser.digit().flatten("digit"));
  }

  @Test
  public void labeled() {
    verify(CharacterParser.digit().labeled("digit"));
  }

  @Test
  public void map() {
    verify(CharacterParser.digit().map(Function.identity()));
  }

  @Test
  public void newline() {
    verify(Parser.newline());
  }

  @Test
  public void not() {
    verify(CharacterParser.digit().not());
  }

  @Test
  public void optional() {
    verify(CharacterParser.digit().optional());
  }

  @Test
  public void or() {
    verify(CharacterParser.digit().or(CharacterParser.word()));
  }

  @Test
  public void plus() {
    verify(CharacterParser.digit().plus());
  }

  @Test
  public void plusGreedy() {
    verify(CharacterParser.digit().plusGreedy(CharacterParser.word()));
  }

  @Test
  public void plusLazy() {
    verify(CharacterParser.digit().plusLazy(CharacterParser.word()));
  }

  @Test
  public void position() {
    verify(Parser.position());
  }

  @Test
  public void repeat() {
    verify(CharacterParser.digit().repeat(2, 3));
  }

  @Test
  public void repeatGreedy() {
    verify(CharacterParser.digit().repeatGreedy(CharacterParser.word(), 2, 3));
  }

  @Test
  public void repeatLazy() {
    verify(CharacterParser.digit().repeatLazy(CharacterParser.word(), 2, 3));
  }

  @Test
  public void seq() {
    verify(CharacterParser.digit().seq(CharacterParser.word()));
  }

  @Test
  public void settable() {
    verify(CharacterParser.digit().settable());
  }

  @Test
  public void star() {
    verify(CharacterParser.digit().star());
  }

  @Test
  public void starGreedy() {
    verify(CharacterParser.digit().starGreedy(CharacterParser.word()));
  }

  @Test
  public void starLazy() {
    verify(CharacterParser.digit().starLazy(CharacterParser.word()));
  }

  @Test
  public void string() {
    verify(StringParser.of("ab"));
  }

  @Test
  public void stringIgnoringCase() {
    verify(StringParser.ofIgnoringCase("ab"));
  }

  @Test
  public void times() {
    verify(CharacterParser.digit().times(2));
  }

  @Test
  public void token() {
    verify(CharacterParser.digit().token());
  }

  @Test
  public void trim() {
    verify(CharacterParser.digit()
        .trim(CharacterParser.of('a'), CharacterParser.of('b')));
  }

  @Test
  public void skip() {
    verify(CharacterParser.digit()
        .skip(CharacterParser.of('['), CharacterParser.of(']')));
  }

  @Test
  public void where() {
    verify(CharacterParser.digit().where((Character c) -> Character.isDigit(c)));
  }

  @Test
  public void whereWithMessage() {
    verify(CharacterParser.digit()
        .where((Character c) -> Character.isDigit(c), "digit expected"));
  }

  @Test
  public void repeatingCharacter() {
    verify(CharacterParser.digit().plusString());
  }

  @Test
  public void starSeparated() {
    verify(CharacterParser.digit().starSeparated(CharacterParser.of(',')));
  }

  @Test
  public void plusSeparated() {
    verify(CharacterParser.digit().plusSeparated(CharacterParser.of(',')));
  }

  @Test
  public void timesSeparated() {
    verify(CharacterParser.digit().timesSeparated(CharacterParser.of(','), 3));
  }

  @Test
  public void repeatSeparated() {
    verify(CharacterParser.digit().repeatSeparated(CharacterParser.of(','), 2, 4));
  }

  @Test
  public void cast() {
    verify(CharacterParser.digit().cast());
  }

  @Test
  public void castWithClass() {
    verify(CharacterParser.digit().cast(Character.class));
  }

  @Test
  public void castList() {
    verify(CharacterParser.digit().star().castList());
  }

  @Test
  public void castListWithClass() {
    verify(CharacterParser.digit().star().castList(Character.class));
  }

  @Test
  public void pick() {
    verify(CharacterParser.digit().star().pick(1));
  }

  @Test
  public void permute() {
    verify(CharacterParser.digit().star().permute(1, 0));
  }
}
