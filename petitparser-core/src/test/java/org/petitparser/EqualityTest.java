package org.petitparser;

import org.junit.Test;
import org.petitparser.parser.Parser;
import org.petitparser.parser.characters.CharacterParser;
import org.petitparser.parser.combinators.DelegateParser;
import org.petitparser.parser.primitive.EpsilonParser;
import org.petitparser.parser.primitive.FailureParser;
import org.petitparser.parser.primitive.StringParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Parser#copy}, {@link Parser#equals(Object)}, and {@link Parser#replace(Parser,
 * Parser)}.
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
  public void failure() {
    verify(FailureParser.withMessage("failure"));
  }

  @Test
  public void flatten() {
    verify(CharacterParser.digit().flatten());
  }

  @Test
  public void map() {
    verify(CharacterParser.digit().map(Function.identity()));
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
    verify(CharacterParser.digit().trim(CharacterParser.of('a'), CharacterParser.of('b')));
  }
}
