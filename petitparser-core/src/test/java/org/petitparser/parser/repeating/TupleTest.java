package org.petitparser.parser.repeating;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link Tuple} hierarchy (Tuple2 to Tuple9).
 */
public class TupleTest {

  @Test
  public void testTuple2() {
    Tuple2<String, Integer> t = Tuple.of("a", 1);
    assertEquals("a", t.first());
    assertEquals(Integer.valueOf(1), t.second());
    assertEquals(2, t.size());
    assertEquals("a", t.get(0));
    assertEquals(1, t.get(1));

    List<Object> expected = Arrays.asList("a", 1);
    assertEquals(expected, t);
    assertEquals(t, expected);
    assertEquals(expected.hashCode(), t.hashCode());
    assertEquals("[a, 1]", t.toString());

    assertEquals(t, new Tuple2<>("a", 1));
    assertNotEquals(t, new Tuple2<>("b", 1));
    assertNotEquals(t, new Tuple2<>("a", 2));
    assertNotEquals(t, Arrays.asList("a", 2));
    assertNotEquals(t, Arrays.asList("a"));
    assertNotEquals(t, "not a list");
    assertNotEquals(t, null);

    Tuple2<String, String> nullTuple1 = new Tuple2<>(null, null);
    Tuple2<String, String> nullTuple2 = new Tuple2<>(null, null);
    assertEquals(nullTuple1, nullTuple2);
    assertEquals(Arrays.asList(null, null), nullTuple1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple2GetNegative() {
    Tuple.of("a", 1).get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple2GetOutOfBounds() {
    Tuple.of("a", 1).get(2);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testTuple2ImmutableAdd() {
    Tuple.of("a", 1).add("c");
  }

  @Test
  public void testTuple3() {
    Tuple3<String, Integer, Double> t = Tuple.of("a", 1, 2.5);
    assertEquals("a", t.first());
    assertEquals(Integer.valueOf(1), t.second());
    assertEquals(Double.valueOf(2.5), t.third());
    assertEquals(3, t.size());
    assertEquals("a", t.get(0));
    assertEquals(1, t.get(1));
    assertEquals(2.5, t.get(2));

    List<Object> expected = Arrays.asList("a", 1, 2.5);
    assertEquals(expected, t);
    assertEquals(t, expected);
    assertEquals(expected.hashCode(), t.hashCode());
    assertEquals("[a, 1, 2.5]", t.toString());

    assertEquals(t, new Tuple3<>("a", 1, 2.5));
    assertNotEquals(t, new Tuple3<>("b", 1, 2.5));
    assertNotEquals(t, new Tuple3<>("a", 2, 2.5));
    assertNotEquals(t, new Tuple3<>("a", 1, 3.5));
    assertNotEquals(t, Arrays.asList("a", 1, 3.5));
    assertNotEquals(t, Arrays.asList("a", 1));
    assertNotEquals(t, "str");
    assertNotEquals(t, null);

    Tuple3<Object, Object, Object> nullT = new Tuple3<>(null, null, null);
    assertEquals(nullT, Arrays.asList(null, null, null));
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple3GetOutOfBounds() {
    Tuple.of("a", 1, 2.5).get(3);
  }

  @Test
  public void testTuple4() {
    Tuple4<String, Integer, Double, Boolean> t = Tuple.of("a", 1, 2.5, true);
    assertEquals("a", t.first());
    assertEquals(Integer.valueOf(1), t.second());
    assertEquals(Double.valueOf(2.5), t.third());
    assertEquals(Boolean.TRUE, t.fourth());
    assertEquals(4, t.size());
    assertEquals("a", t.get(0));
    assertEquals(1, t.get(1));
    assertEquals(2.5, t.get(2));
    assertEquals(true, t.get(3));

    List<Object> expected = Arrays.asList("a", 1, 2.5, true);
    assertEquals(expected, t);
    assertEquals(expected.hashCode(), t.hashCode());
    assertEquals("[a, 1, 2.5, true]", t.toString());

    assertEquals(t, new Tuple4<>("a", 1, 2.5, true));
    assertNotEquals(t, new Tuple4<>("x", 1, 2.5, true));
    assertNotEquals(t, new Tuple4<>("a", 2, 2.5, true));
    assertNotEquals(t, new Tuple4<>("a", 1, 3.5, true));
    assertNotEquals(t, new Tuple4<>("a", 1, 2.5, false));
    assertNotEquals(t, Arrays.asList("a", 1, 2.5, false));
    assertNotEquals(t, Arrays.asList("a", 1, 2.5));
    assertNotEquals(t, null);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple4GetOutOfBounds() {
    Tuple.of("a", 1, 2.5, true).get(4);
  }

  @Test
  public void testTuple5() {
    Tuple5<Integer, Integer, Integer, Integer, Integer> t = Tuple.of(1, 2, 3, 4, 5);
    assertEquals(Integer.valueOf(1), t.first());
    assertEquals(Integer.valueOf(2), t.second());
    assertEquals(Integer.valueOf(3), t.third());
    assertEquals(Integer.valueOf(4), t.fourth());
    assertEquals(Integer.valueOf(5), t.fifth());
    assertEquals(5, t.size());
    for (int i = 0; i < 5; i++) {
      assertEquals(i + 1, t.get(i));
    }

    List<Object> expected = Arrays.asList(1, 2, 3, 4, 5);
    assertEquals(expected, t);
    assertEquals(expected.hashCode(), t.hashCode());
    assertEquals("[1, 2, 3, 4, 5]", t.toString());

    assertEquals(t, new Tuple5<>(1, 2, 3, 4, 5));
    assertNotEquals(t, new Tuple5<>(0, 2, 3, 4, 5));
    assertNotEquals(t, new Tuple5<>(1, 0, 3, 4, 5));
    assertNotEquals(t, new Tuple5<>(1, 2, 0, 4, 5));
    assertNotEquals(t, new Tuple5<>(1, 2, 3, 0, 5));
    assertNotEquals(t, new Tuple5<>(1, 2, 3, 4, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4));
    assertNotEquals(t, "string");
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple5GetOutOfBounds() {
    Tuple.of(1, 2, 3, 4, 5).get(5);
  }

  @Test
  public void testTuple6() {
    Tuple6<Integer, Integer, Integer, Integer, Integer, Integer> t = Tuple.of(1, 2, 3, 4, 5, 6);
    assertEquals(Integer.valueOf(1), t.first());
    assertEquals(Integer.valueOf(2), t.second());
    assertEquals(Integer.valueOf(3), t.third());
    assertEquals(Integer.valueOf(4), t.fourth());
    assertEquals(Integer.valueOf(5), t.fifth());
    assertEquals(Integer.valueOf(6), t.sixth());
    assertEquals(6, t.size());
    for (int i = 0; i < 6; i++) {
      assertEquals(i + 1, t.get(i));
    }

    List<Object> expected = Arrays.asList(1, 2, 3, 4, 5, 6);
    assertEquals(expected, t);
    assertEquals(expected.hashCode(), t.hashCode());
    assertEquals("[1, 2, 3, 4, 5, 6]", t.toString());

    assertEquals(t, new Tuple6<>(1, 2, 3, 4, 5, 6));
    assertNotEquals(t, new Tuple6<>(0, 2, 3, 4, 5, 6));
    assertNotEquals(t, new Tuple6<>(1, 0, 3, 4, 5, 6));
    assertNotEquals(t, new Tuple6<>(1, 2, 0, 4, 5, 6));
    assertNotEquals(t, new Tuple6<>(1, 2, 3, 0, 5, 6));
    assertNotEquals(t, new Tuple6<>(1, 2, 3, 4, 0, 6));
    assertNotEquals(t, new Tuple6<>(1, 2, 3, 4, 5, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 5, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 5));
    assertNotEquals(t, null);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple6GetOutOfBounds() {
    Tuple.of(1, 2, 3, 4, 5, 6).get(6);
  }

  @Test
  public void testTuple7() {
    Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, Integer> t =
        Tuple.of(1, 2, 3, 4, 5, 6, 7);
    assertEquals(Integer.valueOf(1), t.first());
    assertEquals(Integer.valueOf(2), t.second());
    assertEquals(Integer.valueOf(3), t.third());
    assertEquals(Integer.valueOf(4), t.fourth());
    assertEquals(Integer.valueOf(5), t.fifth());
    assertEquals(Integer.valueOf(6), t.sixth());
    assertEquals(Integer.valueOf(7), t.seventh());
    assertEquals(7, t.size());
    for (int i = 0; i < 7; i++) {
      assertEquals(i + 1, t.get(i));
    }

    List<Object> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
    assertEquals(expected, t);
    assertEquals(expected.hashCode(), t.hashCode());
    assertEquals("[1, 2, 3, 4, 5, 6, 7]", t.toString());

    assertEquals(t, new Tuple7<>(1, 2, 3, 4, 5, 6, 7));
    assertNotEquals(t, new Tuple7<>(0, 2, 3, 4, 5, 6, 7));
    assertNotEquals(t, new Tuple7<>(1, 0, 3, 4, 5, 6, 7));
    assertNotEquals(t, new Tuple7<>(1, 2, 0, 4, 5, 6, 7));
    assertNotEquals(t, new Tuple7<>(1, 2, 3, 0, 5, 6, 7));
    assertNotEquals(t, new Tuple7<>(1, 2, 3, 4, 0, 6, 7));
    assertNotEquals(t, new Tuple7<>(1, 2, 3, 4, 5, 0, 7));
    assertNotEquals(t, new Tuple7<>(1, 2, 3, 4, 5, 6, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 5, 6, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 5, 6));
    assertNotEquals(t, 42);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple7GetOutOfBounds() {
    Tuple.of(1, 2, 3, 4, 5, 6, 7).get(7);
  }

  @Test
  public void testTuple8() {
    Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t =
        Tuple.of(1, 2, 3, 4, 5, 6, 7, 8);
    assertEquals(Integer.valueOf(1), t.first());
    assertEquals(Integer.valueOf(2), t.second());
    assertEquals(Integer.valueOf(3), t.third());
    assertEquals(Integer.valueOf(4), t.fourth());
    assertEquals(Integer.valueOf(5), t.fifth());
    assertEquals(Integer.valueOf(6), t.sixth());
    assertEquals(Integer.valueOf(7), t.seventh());
    assertEquals(Integer.valueOf(8), t.eighth());
    assertEquals(8, t.size());
    for (int i = 0; i < 8; i++) {
      assertEquals(i + 1, t.get(i));
    }

    List<Object> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
    assertEquals(expected, t);
    assertEquals(expected.hashCode(), t.hashCode());
    assertEquals("[1, 2, 3, 4, 5, 6, 7, 8]", t.toString());

    assertEquals(t, new Tuple8<>(1, 2, 3, 4, 5, 6, 7, 8));
    assertNotEquals(t, new Tuple8<>(0, 2, 3, 4, 5, 6, 7, 8));
    assertNotEquals(t, new Tuple8<>(1, 0, 3, 4, 5, 6, 7, 8));
    assertNotEquals(t, new Tuple8<>(1, 2, 0, 4, 5, 6, 7, 8));
    assertNotEquals(t, new Tuple8<>(1, 2, 3, 0, 5, 6, 7, 8));
    assertNotEquals(t, new Tuple8<>(1, 2, 3, 4, 0, 6, 7, 8));
    assertNotEquals(t, new Tuple8<>(1, 2, 3, 4, 5, 0, 7, 8));
    assertNotEquals(t, new Tuple8<>(1, 2, 3, 4, 5, 6, 0, 8));
    assertNotEquals(t, new Tuple8<>(1, 2, 3, 4, 5, 6, 7, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 5, 6, 7));
    assertNotEquals(t, null);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple8GetOutOfBounds() {
    Tuple.of(1, 2, 3, 4, 5, 6, 7, 8).get(8);
  }

  @Test
  public void testTuple9() {
    Tuple9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t =
        Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertEquals(Integer.valueOf(1), t.first());
    assertEquals(Integer.valueOf(2), t.second());
    assertEquals(Integer.valueOf(3), t.third());
    assertEquals(Integer.valueOf(4), t.fourth());
    assertEquals(Integer.valueOf(5), t.fifth());
    assertEquals(Integer.valueOf(6), t.sixth());
    assertEquals(Integer.valueOf(7), t.seventh());
    assertEquals(Integer.valueOf(8), t.eighth());
    assertEquals(Integer.valueOf(9), t.ninth());
    assertEquals(9, t.size());
    for (int i = 0; i < 9; i++) {
      assertEquals(i + 1, t.get(i));
    }

    List<Object> expected = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertEquals(expected, t);
    assertEquals(expected.hashCode(), t.hashCode());
    assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9]", t.toString());

    assertEquals(t, new Tuple9<>(1, 2, 3, 4, 5, 6, 7, 8, 9));
    assertNotEquals(t, new Tuple9<>(0, 2, 3, 4, 5, 6, 7, 8, 9));
    assertNotEquals(t, new Tuple9<>(1, 0, 3, 4, 5, 6, 7, 8, 9));
    assertNotEquals(t, new Tuple9<>(1, 2, 0, 4, 5, 6, 7, 8, 9));
    assertNotEquals(t, new Tuple9<>(1, 2, 3, 0, 5, 6, 7, 8, 9));
    assertNotEquals(t, new Tuple9<>(1, 2, 3, 4, 0, 6, 7, 8, 9));
    assertNotEquals(t, new Tuple9<>(1, 2, 3, 4, 5, 0, 7, 8, 9));
    assertNotEquals(t, new Tuple9<>(1, 2, 3, 4, 5, 6, 0, 8, 9));
    assertNotEquals(t, new Tuple9<>(1, 2, 3, 4, 5, 6, 7, 0, 9));
    assertNotEquals(t, new Tuple9<>(1, 2, 3, 4, 5, 6, 7, 8, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 0));
    assertNotEquals(t, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
    assertNotEquals(t, false);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple9GetOutOfBounds() {
    Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9).get(9);
  }

  @Test
  public void testTuplesWithNulls() {
    assertEquals(new Tuple4<>(null, null, null, null), Arrays.asList(null, null, null, null));
    assertEquals(new Tuple5<>(null, null, null, null, null), Arrays.asList(null, null, null, null, null));
    assertEquals(new Tuple6<>(null, null, null, null, null, null), Arrays.asList(null, null, null, null, null, null));
    assertEquals(new Tuple7<>(null, null, null, null, null, null, null), Arrays.asList(null, null, null, null, null, null, null));
    assertEquals(new Tuple8<>(null, null, null, null, null, null, null, null), Arrays.asList(null, null, null, null, null, null, null, null));
    assertEquals(new Tuple9<>(null, null, null, null, null, null, null, null, null), Arrays.asList(null, null, null, null, null, null, null, null, null));

    assertEquals(new Tuple4<>(null, null, null, null).hashCode(), Arrays.asList(null, null, null, null).hashCode());
    assertEquals(new Tuple5<>(null, null, null, null, null).hashCode(), Arrays.asList(null, null, null, null, null).hashCode());
    assertEquals(new Tuple6<>(null, null, null, null, null, null).hashCode(), Arrays.asList(null, null, null, null, null, null).hashCode());
    assertEquals(new Tuple7<>(null, null, null, null, null, null, null).hashCode(), Arrays.asList(null, null, null, null, null, null, null).hashCode());
    assertEquals(new Tuple8<>(null, null, null, null, null, null, null, null).hashCode(), Arrays.asList(null, null, null, null, null, null, null, null).hashCode());
    assertEquals(new Tuple9<>(null, null, null, null, null, null, null, null, null).hashCode(), Arrays.asList(null, null, null, null, null, null, null, null, null).hashCode());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple3NegativeIndex() {
    Tuple.of(1, 2, 3).get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple4NegativeIndex() {
    Tuple.of(1, 2, 3, 4).get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple5NegativeIndex() {
    Tuple.of(1, 2, 3, 4, 5).get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple6NegativeIndex() {
    Tuple.of(1, 2, 3, 4, 5, 6).get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple7NegativeIndex() {
    Tuple.of(1, 2, 3, 4, 5, 6, 7).get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple8NegativeIndex() {
    Tuple.of(1, 2, 3, 4, 5, 6, 7, 8).get(-1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testTuple9NegativeIndex() {
    Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9).get(-1);
  }
}
